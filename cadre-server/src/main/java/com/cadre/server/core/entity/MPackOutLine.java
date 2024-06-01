package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MPackOutLine.TABLE_NAME)
public class MPackOutLine extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** TableName=AD_PackOut_Line */
	public static final String TABLE_NAME = "AD_PackOut_Line";

	public static final int TABLE_ID = 58;
	/** Column name AD_PackOut_ID */
	public static final String COLUMNNAME_AD_PackOut_Line_ID = "AD_PackOut_Line_ID";

	/** Column name AD_PackOut_ID */
	public static final String COLUMNNAME_AD_PackOut_ID = "AD_PackOut_ID";

	/** Column name WhereClause */
	public static final String COLUMNNAME_WhereClause = "WhereClause";

	/** Column name AD_Table_ID */
	public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Column name IsDeletion */
	public static final String COLUMNNAME_IsDeletion = "IsDeletion";

	/**
	 * Set Table.
	 * 
	 * @param AD_Table_ID Database Table information
	 */
	public void setAD_Table_ID(int AD_Table_ID) {
		if (AD_Table_ID < 1)
			setValueNoCheck(COLUMNNAME_AD_Table_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
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
	 * Set Export PackOut_ID.
	 * 
	 * @param PackOut_ID
	 */
	public void setAD_PackOut_ID(int p_PackOutID) {
		if (p_PackOutID < 1)
			setValueNoCheck(COLUMNNAME_AD_PackOut_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_PackOut_ID, Integer.valueOf(p_PackOutID));
	}

	/**
	 * Get PackOut_ID
	 * 
	 * @return PackOut_ID
	 */
	public int getAD_PackOut_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_PackOut_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set Deletion
	 * 
	 * @param deletion deletion
	 */
	public final void setIsDeletion(boolean active) {
		setValueNoCheck(COLUMNNAME_IsDeletion, Boolean.valueOf(active));
	} // setDeletion

	/**
	 * Is Deletion
	 * 
	 * @return is deletion
	 */
	public final boolean isDeletion() {
		Boolean bb = (Boolean) getValueNoCheck(COLUMNNAME_IsDeletion);
		if (bb != null)
			return bb.booleanValue();
		return false;
	} // isDeletion

	/**
	 * Set Sql WHERE.
	 * 
	 * @param WhereClause Fully qualified SQL WHERE clause
	 */
	public void setWhereClause(String WhereClause) {
		setValueNoCheck(COLUMNNAME_WhereClause, WhereClause);
	}

	/**
	 * Get Sql WHERE.
	 * 
	 * @return Fully qualified SQL WHERE clause
	 */
	public String getWhereClause() {
		return (String) getValueNoCheck(COLUMNNAME_WhereClause);
	}
}
