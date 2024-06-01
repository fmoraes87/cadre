package com.cadre.server.core.init;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.annotation.CadreExtensionInitializer;
import com.cadre.server.core.cronjob.Scheduler;
import com.cadre.server.core.entity.MCronJob;
import com.cadre.server.core.persistence.jdbc.StatementFactory;
import com.cadre.server.core.service.ModelService;

@CadreExtensionInitializer
public class CadreExtensionBootstrap extends AbstractCadreBootstrap {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CadreExtensionBootstrap.class);

	private static final String EXTENSION_NAME = "cadre-core";
	private static final String PACKAGENAME_MODEL = "com.cadre.server.core.entity";
	private static final String PACKAGENAME_POPULATORS = "com.cadre.server.core.populators;com.cadre.server.core.builders";

	public CadreExtensionBootstrap() {
		LOGGER.info("Loading:" + CadreExtensionBootstrap.class);

	}

	public void initCustomResources() {
		LOGGER.info("initCustomResources");		
		initScheduler();
	}
	
	private void initScheduler() {
		LOGGER.info("initScheduler");
			
		final ModelService modelService =CDI.current().select(ModelService.class).get();
		
		List<MCronJob> activeJobs = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ad_client_id,ad_org_id,ad_cronjob_id ");
		sql.append(" FROM AD_CronJob mv ");
		sql.append(" WHERE mv.isActive='Y'  ");

		try (final PreparedStatement pstmt = StatementFactory.newCPreparedStatement(getTrxName(), sql.toString())) {

			try (ResultSet rs = pstmt.executeQuery()) {
				
				while (rs.next()) {
					CadreEnv.setContextValue(CadreEnv.AD_CLIENT_ID, rs.getInt("ad_client_id"));
					CadreEnv.setContextValue(CadreEnv.AD_ORG_ID, rs.getInt("ad_org_id"));
					MCronJob job = modelService.getPO(getTrxName(), MCronJob.TABLE_NAME, rs.getInt("ad_cronjob_id"));
					activeJobs.add(job);
					
				}
				
			}

		} catch (SQLException e) {
			LOGGER.error("initScheduler()", e);
			System.exit(1);
		}
		
		
		if (CollectionUtils.isNotEmpty(activeJobs)) {
			LOGGER.info("Adding Jobs");

			activeJobs.forEach( cronJob -> Scheduler.get().addJob(cronJob));
		}

        
	}
	
	@Override
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	@Override
	public String getModelPackageName() {
		return PACKAGENAME_MODEL;
	}

	@Override
	public String getPopulatorsPackageName() {
		return PACKAGENAME_POPULATORS;
	}


}
