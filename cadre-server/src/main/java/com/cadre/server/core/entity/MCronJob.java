package com.cadre.server.core.entity;

import java.sql.Timestamp;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MCronJob.TABLE_NAME)
public class MCronJob extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_CronJob";
	public static final int TABLE_ID = 48;

	/** Column name AD_CronJob_ID */
	public static final String COLUMNNAME_AD_CronJob_ID = "AD_CronJob_ID";
	/** Column name CurrentStatus */
	public static final String COLUMNNAME_CurrentStatus = "CurrentStatus";
	/** Column name LastResult */
	public static final String COLUMNNAME_LastResult = "LastResult";
	/** Column name LastEndTime */
	public static final String COLUMNNAME_LastEndTime = "LastEndTime";
	/** Column name LastStartTime */
	public static final String COLUMNNAME_LastStartTime = "LastStartTime";
	/** Column name CronExpression */
	public static final String COLUMNNAME_CronExpression = "CronExpression";
	/** Column name AD_JobDefinition_ID */
	public static final String COLUMNNAME_AD_JobDefinition_ID = "AD_JobDefinition_ID";
	/** Column name AD_User_ID */
	public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";
	
	/**
	 * 
	 * @return
	 */
	public int getAD_User_ID() {
		Integer ii = (Integer) getValueOfColumn(COLUMNNAME_AD_User_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getAD_CronJob_ID() {
		Integer ii = (Integer) getValueOfColumn(COLUMNNAME_AD_CronJob_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public int getAD_JobDefinition_ID() {
		Integer ii = (Integer) getValueOfColumn(COLUMNNAME_AD_JobDefinition_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	
	/** Set Cron Scheduling Pattern.
		@param CronPattern 
		Cron pattern to define when the process should be invoked.
	  */
	public void setCronPattern (String CronPattern)
	{
		setValueNoCheck (COLUMNNAME_CronExpression, CronPattern);
	}

	/** Get Cron Scheduling Pattern.
		@return Cron pattern to define when the process should be invoked.
	  */
	public String getCronPattern () 
	{
		return (String)getValueNoCheck(COLUMNNAME_CronExpression);
	}
	
	
	/**
	 * Set Date last run.
	 * 
	 * @param DateLastRun Date the process was last run.
	 */
	public void setLastStartTime(Timestamp DateLastRun) {
		setValueNoCheck(COLUMNNAME_LastStartTime, DateLastRun);
	}

	/**
	 * Get Date last run.
	 * 
	 * @return Date the process was last run.
	 */
	public Timestamp getLastStartTime() {
		return (Timestamp) getValueNoCheck(COLUMNNAME_LastStartTime);
	}



	/**
	 * Set Date last run.
	 * 
	 * @param DateLastRun Date the process was last run.
	 */
	public void setLastEndTime(Timestamp DateLastRun) {
		setValueNoCheck(COLUMNNAME_LastEndTime, DateLastRun);
	}

	/**
	 * Get Date last run.
	 * 
	 * @return Date the process was last run.
	 */
	public Timestamp getLastEndTime() {
		return (Timestamp) getValueNoCheck(COLUMNNAME_LastEndTime);
	}


}
