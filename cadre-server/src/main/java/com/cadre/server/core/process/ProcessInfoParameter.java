package com.cadre.server.core.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProcessInfoParameter {
	
    public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm";


	private String name;
	private Object value;

	public ProcessInfoParameter() {
	
	}
	
	public ProcessInfoParameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
	
	/**
	 * Method getParameter as Int
	 * @return Object
	 */
	public int getParameterAsInt ()
	{
		if (value == null)
			return 0;
		if (value instanceof Number)
			return ((Number)value).intValue();
		BigDecimal bd = new BigDecimal(value.toString());
		return bd.intValue();
	}	//	getParameterAsInt
	
	/**
	 * Method getParameter as Boolean
	 * @return boolean value
	 */
	public boolean getParameterAsBoolean ()
	{
		if (value == null)
			return false;
		if (value instanceof Boolean)
			return ((Boolean)value).booleanValue();
		return "Y".equals(value);
	}
	
	/**
	 * Method getParameter as Timestamp
	 * @return Object
	 */
	public Timestamp getParameterAsTimestamp()
	{
		if (value == null)
			return null;
		if (value instanceof Timestamp)
			return (Timestamp) value;
		
		if (value instanceof String) {
			try {
				DateFormat formatter = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
				Date date = formatter.parse((String) value);
				return new Timestamp(date.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		
		
		return null;
	}	//	getParameterAsTimestamp



}
