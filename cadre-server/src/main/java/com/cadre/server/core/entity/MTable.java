package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MTable.TABLE_NAME)
public class MTable extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_Table";
	public static final int TABLE_ID = 4;
    public static final String TRL_PREFIX = "_TRL";

	/** Column name AD_Org_ID */
	public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
	/** Column name AD_Table_ID */
	public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";
	/** Column name AD_Table_UU */
	public static final String COLUMNNAME_AD_Table_UU = "AD_Table_UU";
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";
	/** Column name Help */
	public static final String COLUMNNAME_Help = "Help";
	/** Column name IsChangeLog */
	public static final String COLUMNNAME_IsChangeLog = "IsChangeLog";
	/** Column name IsDeleteable */
	public static final String COLUMNNAME_IsDeleteable = "IsDeleteable";
	/** Column name IsHighVolume */
	public static final String COLUMNNAME_IsHighVolume = "IsHighVolume";
	/** Column name IsSecurityEnabled */
	public static final String COLUMNNAME_IsSecurityEnabled = "IsSecurityEnabled";
	/** Column name IsView */
	public static final String COLUMNNAME_IsView = "IsView";
	/** Column name LoadSeq */
	public static final String COLUMNNAME_LoadSeq = "LoadSeq";
	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";
	/** Column name TableName */
	public static final String COLUMNNAME_TableName = "TableName";
	/** Column name AD_Extension_ID */
	public static final String COLUMNNAME_AD_Extension_ID = "AD_Extension_ID";
	
	public void setTableName(String tableName) {
		this.setValueNoCheck(COLUMNNAME_TableName, tableName);
	}

	public String getTableName() {
		return (String) this.getValueNoCheck(COLUMNNAME_TableName);
	}

	/**
	 * Get Table.
	 * 
	 * @return Database Table information
	 */
	public int getAD_Table_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set AD_Extension_ID
	 * 
	 * @param AD_Extension_ID
	 */
	public void setAD_Extension_ID(Integer p_AD_Extension_ID) {
		setValueNoCheck(COLUMNNAME_AD_Extension_ID, p_AD_Extension_ID);
	}

	/**
	 * Set Name.
	 * 
	 * @param Name Alphanumeric identifier of the entity
	 */
	public void setName(String Name) {
		setValueNoCheck(COLUMNNAME_Name, Name);
	}

	/**
	 * Set View.
	 * 
	 * @param IsView This is a view
	 */
	public void setIsView(boolean IsView) {
		setValueNoCheck(COLUMNNAME_IsView, Boolean.valueOf(IsView));
	}

	public void setAD_Extension_ID(String p_Extension) {
		// TODO Auto-generated method stub
		
	}

}
