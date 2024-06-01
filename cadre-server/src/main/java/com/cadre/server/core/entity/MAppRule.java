package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MAppRule.TABLE_NAME)
public class MAppRule extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    /** TableName=AD_AppRole */
    public static final String TABLE_NAME = "AD_AppRule";
    
    /** Column name AD_Variable_ID */
    public static final String COLUMNNAME_AD_AppRole_ID = "AD_AppRule_ID";

	/** Column name AD_OAuth_Client_ID */
	public static final String COLUMNNAME_AD_App_ID = "AD_App_ID";
	
	/** Column name AD_Table_ID */
	public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Column name Expression */
	public static final String COLUMNNAME_EXPRESSION = "Expression";

	/** Column name AD_Role_ID */
	public static final String COLUMNNAME_AD_Role_ID = "AD_Role_ID";
	
	/**
	 * 
	 * @param AD_App_ID
	 */
	public void setAD_App_ID(int AD_App_ID) {
		if (AD_App_ID < 0)
			setValueNoCheck(COLUMNNAME_AD_App_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_App_ID, Integer.valueOf(AD_App_ID));
	}

	/**
	 * 
	 * @return
	 */
	public int getAD_App_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_App_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}	
	
	
	/**
	 * 
	 * @param 
	 */
	public void setAD_Table_ID(int AD_Table_ID) {
		if (AD_Table_ID < 0)
			setValueNoCheck(COLUMNNAME_AD_Table_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
	}

	/**
	 * 
	 * @return
	 */
	public int getAD_Table_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}	

	
	/**
	 * 
	 * @param 
	 */
	public void setAD_Role_ID(int p_AD_Role_ID) {
		if (p_AD_Role_ID < 0)
			setValueNoCheck(COLUMNNAME_AD_Role_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_Role_ID, Integer.valueOf(p_AD_Role_ID));
	}

	/**
	 * 
	 * @return
	 */
	public int getAD_Role_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Role_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}	
	
	
	/** Get Rule.
		@return Rule
	  */
	public String getExpression () 
	{
		return (String)getValueNoCheck(COLUMNNAME_EXPRESSION);
	}

}
