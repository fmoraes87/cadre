package com.cadre.server.core.cronjob;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.MCronJob;
import com.cadre.server.core.entity.MJobDefinition;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.jdbc.RDBMS;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.process.CadreProcess;
import com.cadre.server.core.process.ScriptingEngine;
import com.cadre.server.core.process.SvrProcessEngine;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.TimeUtil;

import cron.CronExpression;
import cron.CronSchedule;

public class Scheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

	
	private static final String SQL_UPDATE_LAST_END_TIME = "UPDATE CADRE.AD_CronJob set LastEndTime=? where ad_cronjob_id= ?";
	private static final String SQL_UPDATE_CURRENT_STATUS = "UPDATE CADRE.AD_CronJob set CurrentStatus=? where ad_cronjob_id= ?";
	private static final String SQL_UPDATE_LASTSTART_TIME = "UPDATE CADRE.AD_CronJob set LastStartTime=? where ad_cronjob_id= ?";
	
	
	private ScheduledExecutorService executor;
	private CronSchedule schedule;

	private ModelService modelService;
	private SvrProcessEngine svrProcessEngine;

	// Inner class to provide instance of class
	private static class Singleton {
		private static final Scheduler INSTANCE = new Scheduler();
	}

	public static Scheduler get() {
		return Singleton.INSTANCE;
	}

	/**************************************************************************
	 * Constructor. Scheduler
	 */
	private Scheduler() {
		init();

	}

	private void init() {
		modelService = CDI.current().select(ModelService.class).get();
		svrProcessEngine = CDI.current().select(SvrProcessEngine.class).get();
		executor = Executors.newScheduledThreadPool(4);
		schedule = new CronSchedule(executor, true);
		schedule.start();
	}

	public void addJob(final MCronJob cronJob) {
		LOGGER.info("Adding job" + cronJob + " ID:" + cronJob.getAD_CronJob_ID());
		final CronExpression expression = CronExpression.parser() // S M H D M A
				.withSecondsField(true)
				.parse(cronJob.getCronPattern());

		final MJobDefinition jobDefinition = modelService.getPO(cronJob.get_TrxName(), MJobDefinition.TABLE_NAME,
				cronJob.getAD_JobDefinition_ID());
		LOGGER.info("Job Definition: " + jobDefinition.getName());

		final Integer cronJobId = cronJob.getAD_CronJob_ID(); 
		final Integer adUserId = cronJob.getAD_User_ID();
		final String processName = jobDefinition.getProcedureName();
		final Integer scriptingId = jobDefinition.getAD_Scripting_ID();
		
		// Lambda Runnable
		Runnable task = new Runnable() {
			public void run() {
				
				CadreEnv.setCtx(new Properties());
				
				long p_startWork = System.currentTimeMillis();
				
				String strActive = RDBMS.getSQLValueStringEx(null, "SELECT IsActive FROM CADRE.AD_CronJob where AD_CronJob_ID=?", cronJobId);
				if (POModel.YES_VALUE.equals(strActive)) {
					
					RDBMS.executeUpdate(null, SQL_UPDATE_LASTSTART_TIME,
							new Object[] { new Timestamp(p_startWork),cronJobId }, 0);
					
					LOGGER.info(processName + " is running");
					
					CadreEnv.setContextValue(CadreEnv.AD_USER_ID, adUserId);
					CadreEnv.setContextValue(CadreEnv.AD_CLIENT_ID, cronJob.getAD_Client_ID());
					CadreEnv.setContextValue(CadreEnv.AD_ORG_ID, cronJob.getAD_Org_ID());
					
					Trx trx = Trx.get(Trx.createTrxName(processName), true);
					try {
						
						if (scriptingId > 0) {
							ScriptingEngine.execute(trx.getTrxName(),scriptingId);
							
						}else {//TODO - Load from ad_process_id
							CadreProcess proc = svrProcessEngine.getSvrProcess(processName);
							proc.execute(trx.getTrxName());							
						}
						
						RDBMS.executeUpdate(null, SQL_UPDATE_CURRENT_STATUS,
								new Object[] {"SUCCESSFUL",cronJobId }, 0);
						
					} catch (Throwable ex) {
						if (trx != null) {
							trx.rollback();
							trx.close();
							trx = null;
						}
						
						LOGGER.error("Lambda Runnable ("+processName+")", ex);
						RDBMS.executeUpdate(null, SQL_UPDATE_CURRENT_STATUS,new Object[] {"ERROR",cronJobId }, 0);
					}finally {
						if (trx != null) {
							trx.commit();
							trx.close();
						}
					}
						
					LOGGER.info("Run # " + processName + StringUtils.SPACE + TimeUtil.formatElapsed(new Timestamp(p_startWork)));
					
					long now = System.currentTimeMillis();
					
					RDBMS.executeUpdate(null, SQL_UPDATE_LAST_END_TIME,
							new Object[] { new Timestamp(now),cronJobId }, 0);
					
				}else {
					LOGGER.info("Removing scheduler " + processName);

					schedule.remove(expression, this);
				}
			}
			
		};

		LOGGER.info("Adding scheduler " + processName);
		schedule.add(expression, task);

	}

}
