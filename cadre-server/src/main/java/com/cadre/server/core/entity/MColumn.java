package com.cadre.server.core.entity;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MColumn.TABLE_NAME)
public class MColumn extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_Column";
	public static final int TABLE_ID = 5;

	/** Column name AD_Process_ID */
	public static final String COLUMNNAME_AD_Process_ID = "AD_Process_ID";
	/** Column name AD_Column_ID */
	public static final String COLUMNNAME_AD_Column_ID = "AD_Column_ID";
	/** Column name AD_Column_UU */
	public static final String COLUMNNAME_AD_Column_UU = "AD_Column_UU";
	/** Column name AD_Reference_ID */
	public static final String COLUMNNAME_AD_Reference_ID = "AD_Reference_ID";
	/** Column name AD_Reference_ID */
	public static final String COLUMNNAME_AD_Reference_Value_ID = "AD_Reference_Value_ID";
	/** Column name AD_Table_ID */
	public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";
	/** Column name ColumnName */
	public static final String COLUMNNAME_ColumnName = "ColumnName";
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";
	/** Column name Help */
	public static final String COLUMNNAME_Help = "Help";
	/** Column name IsKey */
	public static final String COLUMNNAME_IsKey = "IsKey";
	/** Column name IsTranslatable */
	public static final String COLUMNNAME_IsTranslatable = "IsTranslatable";
	/** Column name IsMandatory */
	public static final String COLUMNNAME_IsMandatory = "IsMandatory";
	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";
	/** Column name AD_Extension_ID */
	public static final String COLUMNNAME_AD_Extension_ID = "AD_Extension_ID";
	/** Column name Updatable */
	public static final String COLUMNNAME_Updatable = "Updatable";
	
	public MColumn() {
		
		addGetActionListener(COLUMNNAME_IsKey, new Callable<Boolean>() {
			public Boolean call() {
				return isKey();
			}
		});
		
		addGetActionListener(COLUMNNAME_IsTranslatable, new Callable<Boolean>() {
			public Boolean call() {
				return isTranslatable();
			}
		});
		
		addGetActionListener(COLUMNNAME_AD_Reference_ID, new Callable<Integer>() {
			public Integer call() {
				return getAD_Reference_ID();
			}
		});
		
		
		addSetActionListener(COLUMNNAME_AD_Reference_ID, new Consumer<Integer>() {
			public void accept(Integer p_AD_Reference_ID) {
				setAD_Reference_ID(p_AD_Reference_ID);
			}
		}); 
		
		addSetActionListener(COLUMNNAME_AD_Table_ID, new Consumer<Integer>() {
			public void accept(Integer p_AD_Table_ID) {
				setAD_Table_ID(p_AD_Table_ID);
			}
		}); 
	}
	/**
	 * Get DB Column Name.
	 * 
	 * @return Name of the column in the database
	 */
	public String getColumnName() {
		return (String) getValueNoCheck(COLUMNNAME_ColumnName);
	}

	/**
	 * Get Key column.
	 * 
	 * @return This column is the key in this table
	 */
	public boolean isKey() {
		Object oo = getValueNoCheck(COLUMNNAME_IsKey);
		if (oo != null) {
			if (oo instanceof Boolean) {
				return ((Boolean) oo).booleanValue();
			}
			return POModel.YES_VALUE.equals(oo);
		}
		return false;
	}
	

	/**
	 * Get Key column.
	 * 
	 * @return This column is translatable
	 */
	public boolean isTranslatable() {
		Object oo = getValueNoCheck(COLUMNNAME_IsTranslatable);
		if (oo != null) {
			if (oo instanceof Boolean) {
				return ((Boolean) oo).booleanValue();
			}
			return POModel.YES_VALUE.equals(oo);
		}
		return false;
	}

	/**
	 * Get Reference.
	 * 
	 * @return System Reference and Validation
	 */
	public int getAD_Reference_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Reference_ID);
		if (ii == null) {
			return 0;
		}
		return ii.intValue();
	}
	
	/**
	 * Get Reference.
	 * 
	 * @return System Reference and Validation
	 */
	public int getAD_Reference_Value_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Reference_Value_ID);
		if (ii == null) {
			return 0;
		}
		return ii.intValue();
	}


	/**
	 * Get AD_Process.
	 * 
	 * @return AD_Process
	 */
	public int getAD_Process_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Process_ID);
		if (ii == null) {
			return 0;
		}
		return ii.intValue();
	}
	
	/**
	 * Set AD_Process_ID.
	 * 
	 * @param AD_Process_ID
	 */
	public void setAD_Process_ID(int p_AD_Process_ID) {
		if (p_AD_Process_ID < 1)
			setValueNoCheck(COLUMNNAME_AD_Process_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_Process_ID, Integer.valueOf(p_AD_Process_ID));
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
	 * Set DB Column Name.
	 * 
	 * @param ColumnName Name of the column in the database
	 */
	public void setColumnName(String ColumnName) {
		setValueNoCheck(COLUMNNAME_ColumnName, ColumnName);
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
	 * Set Mandatory.
	 * 
	 * @param IsMandatory Data entry is required in this column
	 */
	public void setIsMandatory(boolean IsMandatory) {
		setValueNoCheck(COLUMNNAME_IsMandatory, Boolean.valueOf(IsMandatory));
	}

	/**
	 * Set Reference.
	 * 
	 * @param AD_Reference_ID System Reference and Validation
	 */
	public void setAD_Reference_ID(int p_AD_Reference_ID) {
		if (p_AD_Reference_ID < 1)
			setValueNoCheck(COLUMNNAME_AD_Reference_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_Reference_ID, Integer.valueOf(p_AD_Reference_ID));
	}

	/**
	 * Set Table.
	 * 
	 * @param AD_Table_ID Database Table information
	 */
	public void setAD_Table_ID(int p_AD_Table_ID) {
		if (p_AD_Table_ID < 1)
			setValueNoCheck(COLUMNNAME_AD_Table_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_Table_ID, Integer.valueOf(p_AD_Table_ID));
	}

	/**
	 * Set IsKey.
	 * 
	 * @param IsMandatory Data entry is required in this column
	 */
	public void setIsKey(boolean isKey) {
		setValueNoCheck(COLUMNNAME_IsKey, Boolean.valueOf(isKey));
	}

	/**
	 * Updatable
	 * 
	 * @return This column is updatable
	 */
	public boolean isUpdatable() {
		Object oo = getValueNoCheck(COLUMNNAME_Updatable);
		if (oo != null) {
			if (oo instanceof Boolean) {
				return ((Boolean) oo).booleanValue();
			}
			return POModel.YES_VALUE.equals(oo);
		}
		return false;
	}

}
