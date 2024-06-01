package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MPackOut.TABLE_NAME)
public class MPackOut extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** TableName=AD_PackOut */
	public static final String TABLE_NAME = "AD_PackOut";

	public static final int TABLE_ID = 57;

	/** Column name AD_PackOut_ID */
	public static final String COLUMNNAME_AD_PackOut_ID = "AD_PackOut_ID";

	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";

	/** Column name Version */
	public static final String COLUMNNAME_Version = "Version";

	/** Column name Help */
	public static final String COLUMNNAME_Help = "Help";

	/** Column name Instructions */
	public static final String COLUMNNAME_Instructions = "Instructions";

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
	 * Set Name.
	 * 
	 * @param Name Alphanumeric identifier of the entity
	 */
	public void setName(String Name) {
		setValueNoCheck(COLUMNNAME_Name, Name);
	}

	/**
	 * Get Name.
	 * 
	 * @return Alphanumeric identifier of the entity
	 */
	public String getName() {
		return (String) getValueNoCheck(COLUMNNAME_Name);
	}

	/**
	 * Set Comment/Help.
	 * 
	 * @param Help Comment or Hint
	 */
	public void setHelp(String Help) {
		setValueNoCheck(COLUMNNAME_Help, Help);
	}

	/**
	 * Get Comment/Help.
	 * 
	 * @return Comment or Hint
	 */
	public String getHelp() {
		return (String) getValueNoCheck(COLUMNNAME_Help);
	}

	/**
	 * Set Version.
	 * 
	 * @param Version Version of the table definition
	 */
	public void setVersion(String Version) {
		setValueNoCheck(COLUMNNAME_Version, Version);
	}

	/**
	 * Get Version.
	 * 
	 * @return Version of the table definition
	 */
	public String getVersion() {
		return (String) getValueNoCheck(COLUMNNAME_Version);
	}

	/**
	 * SetInstructions.
	 * 
	 * @param Instructions
	 */
	public void setInstructions(String instructions) {
		setValueNoCheck(COLUMNNAME_Instructions, instructions);
	}

	/**
	 * Get Instructions.
	 * 
	 * @return Instruction or Hint
	 */
	public String getInstructions() {
		return (String) getValueNoCheck(COLUMNNAME_Instructions);
	}
	
	
}
