package com.cadre.server.core.entity;

import java.io.InputStream;
import java.util.function.Consumer;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MMedia.TABLE_NAME)
public class MMedia extends POModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** TableName=AD_Media */
	public static final String TABLE_NAME = "AD_Media";
	/** AD_Table_ID= */
	public static final int TABLE_ID = 28;
	  /** Column name AD_Media_ID */
    public static final String COLUMNNAME_AD_Media_ID = "AD_Media_ID";
    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";
	  /** Column name AD_Media_ID */
    public static final String COLUMNNAME_AD_MediaFormat_ID = "AD_MediaFormat_ID";
	  /** Column name AD_MediaFolder_ID */
    public static final String COLUMNNAME_AD_MediaFolder_ID = "AD_MediaFolder_ID";
    
	/** The Data				*/
	private InputStream 	m_data = null;
    
    public MMedia() {
		addSetActionListener(COLUMNNAME_AD_MediaFormat_ID, new Consumer<Integer>() {
			public void accept(Integer p_AD_Reference_ID) {
				setAD_MediaFormat_ID(p_AD_Reference_ID);
			}
		}); 
		
		addSetActionListener(COLUMNNAME_AD_MediaFolder_ID, new Consumer<Integer>() {
			public void accept(Integer p_AD_Table_ID) {
				setAD_MediaFolder_ID(p_AD_Table_ID);
			}
		}); 
    }
    
	/**
	 * Get MediaFolder.
	 * 
	 * @return Database Table information
	 */
	public int getAD_MediaFolder_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_MediaFolder_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

    /**
     * Get FileName
     * @return
     */
	public String getValue () 
	{
		return (String)getValueNoCheck(COLUMNNAME_Value);
	}

    /**
     * Set Value
     * @param value
     */
	public void setValue(String value) {
		setValueNoCheck(COLUMNNAME_Value, value);
	}

	/**
	 * Set MediaFormat.
	 * 
	 * @param p_AD_MediaFormat_ID
	 */
	public void setAD_MediaFormat_ID(int p_AD_MediaFormat_ID) {
		if (p_AD_MediaFormat_ID < 1)
			setValueNoCheck(COLUMNNAME_AD_MediaFormat_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_MediaFormat_ID, Integer.valueOf(p_AD_MediaFormat_ID));
	}
	
	/**
	 * Set MediaFolder.
	 * 
	 * @param p_AD_MediaFolder_ID 
	 */
	public void setAD_MediaFolder_ID(int p_AD_MediaFolder_ID) {
		if (p_AD_MediaFolder_ID < 1)
			setValueNoCheck(COLUMNNAME_AD_MediaFolder_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_MediaFolder_ID, Integer.valueOf(p_AD_MediaFolder_ID));
	}

	/**
	 * @return Returns the data.
	 */
	public InputStream getData ()
	{
		return m_data;
	}
	/**
	 * @param data The data to set.
	 */
	public void setData (InputStream data)
	{
		m_data = data;
	}
	
}
