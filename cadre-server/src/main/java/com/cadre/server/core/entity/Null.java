package com.cadre.server.core.entity;


/**
 * 	Database Null Indicator 
 */
public class Null
{
	/** Singleton				*/
	public static final Null	NULL = new Null();
	
	/**
	 * 	NULL Constructor
	 */
	private Null ()
	{
	}	//	Null
	
	/**
	 * 	String Representation
	 *	@return info
	 */
	public String toString ()
	{
		return "NULL";
	} //	toString
	
}	//	Null