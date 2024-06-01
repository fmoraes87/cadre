package com.cadre.server.core.util;


public final class DisplayType
{
	/** Display Type 10	String	*/
	public static final int String     = SystemIDs.REFERENCE_DATATYPE_STRING;
	/** Display Type 11	Integer	*/
	public static final int Integer    = SystemIDs.REFERENCE_DATATYPE_INTEGER;
	/** Display Type 12	Amount	*/
	public static final int Amount     = SystemIDs.REFERENCE_DATATYPE_AMOUNT;
	/** Display Type 13	ID	*/
	public static final int ID         = SystemIDs.REFERENCE_DATATYPE_ID;
	/** Display Type 14	Text	*/
	public static final int Text       = SystemIDs.REFERENCE_DATATYPE_TEXT;
	/** Display Type 15	Date	*/
	public static final int Date       = SystemIDs.REFERENCE_DATATYPE_DATE;
	/** Display Type 16	DateTime	*/
	public static final int DateTime   = SystemIDs.REFERENCE_DATATYPE_DATETIME;
	/** Display Type 17	List	*/
	public static final int List       = SystemIDs.REFERENCE_DATATYPE_LIST;
	/** Display Type 18	Table	*/
	public static final int Table      = SystemIDs.REFERENCE_DATATYPE_TABLE;
	/** Display Type 19	TableDir	*/
	public static final int TableDir   = SystemIDs.REFERENCE_DATATYPE_TABLEDIR;
	/** Display Type 20	YN	*/
	public static final int YesNo      = SystemIDs.REFERENCE_DATATYPE_YES_NO;
	/** Display Type 22	Number	*/
	public static final int Number     = SystemIDs.REFERENCE_DATATYPE_NUMBER;
	/** Display Type 23	BLOB	*/
	public static final int Binary     = SystemIDs.REFERENCE_DATATYPE_BINARY;
	/** Display Type 24	Time	*/
	public static final int Time       = SystemIDs.REFERENCE_DATATYPE_TIME;
	/** Display Type 36	CLOB	*/
	public static final int Collection   = SystemIDs.REFERENCE_DATATYPE_COLLECTION;
	/** Display Type 42	CLOB	*/
	public static final int Password   = SystemIDs.REFERENCE_PASSWORD;
	/** Display Type 28	CLOB	*/
	public static final int Map   = SystemIDs.REFERENCE_DATATYPE_MAP;	
	/**
	 *	Returns true if (numeric) ID (Table, Search, Account, ..).
	 *  (stored as Integer)
	 *  @param displayType Display Type
	 *  @return true if ID
	 */
	public static boolean isID (int displayType)
	{
		if (displayType == ID || displayType == Table || displayType == TableDir) {
			return true;			
		}
		
		
		return false;
	}	//	isID

	/**
	 *	Returns true, if DisplayType is numeric (Amount, Number, Quantity, Integer).
	 *  (stored as BigDecimal)
	 *  @param displayType Display Type
	 *  @return true if numeric
	 */
	public static boolean isNumeric(int displayType)
	{
		if (displayType == Amount || displayType == Number 
			|| displayType == Integer ) {
			return true;			
		}
		
		
		return false;
	}	//	isNumeric
	
	/**
	 * 	Get Default Precision.
	 * 	Used for databases who cannot handle dynamic number precision.
	 *	@param displayType display type
	 *	@return scale (decimal precision)
	 */
	public static int getDefaultPrecision(int displayType)
	{
		if (displayType == Amount)
			return 2;
		if (displayType == Number)
			return 6;
		return 0;
	}	//	getDefaultPrecision

	public static Class<?> getClass (int displayType, boolean yesNoAsBoolean)
	{
		if (displayType == List  || displayType==String || displayType== Text || displayType==Password
				|| displayType==Collection || displayType==Map)
			return String.class;
		else if (isID(displayType) || displayType == Integer)    //  note that Integer is stored as BD
			return Integer.class;
		else if (isNumeric(displayType))
			return java.math.BigDecimal.class;
		else if (isDate(displayType))
			return java.sql.Timestamp.class;
		else if (displayType == YesNo)
			return yesNoAsBoolean ? Boolean.class : String.class;
		else if (isLOB(displayType))	//	CLOB is String
			return byte[].class;

		//
		throw new IllegalArgumentException("Type for displaytype= " + displayType + " not found ");
	}   //  getClass
	
	
	/**
	 *	Returns true if DisplayType is a Date.
	 *  (stored as Timestamp)
	 *  @param displayType Display Type
	 *  @return true if date
	 */
	public static boolean isDate (int displayType)
	{
		if (displayType == Date || displayType == DateTime || displayType == Time) {
			return true;			
		}

		return false;
	}	//	isDate
	
	/**
	 * 	Returns true if DisplayType is a Large Object
	 *	@param displayType Display Type
	 *	@return true if LOB
	 */
	public static boolean isLOB (int displayType)
	{
		if (displayType == Binary) {
			return true;			
		}

		return false;
	}	//	isLOB
	
	/**
	 * 	Returns true if DisplayType is a Large Object
	 *	@param displayType Display Type
	 *	@return true if LOB
	 */
	public static boolean isTable (int displayType)
	{
		if (displayType == Table) {
			return true;			
		}

		return false;
	}	//	isLOB


}
