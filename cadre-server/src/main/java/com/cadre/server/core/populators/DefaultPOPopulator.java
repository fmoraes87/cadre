package com.cadre.server.core.populators;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import com.cadre.server.core.converters.Populator;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;

public class DefaultPOPopulator implements Populator<POModel, Entity> {

	 // Inner class to provide instance of class 
	private static class Singleton {
		private static final DefaultPOPopulator INSTANCE = new DefaultPOPopulator();
	}

	public static DefaultPOPopulator get() {
		return Singleton.INSTANCE;
	}	
	
	private DefaultPOPopulator() { }
	

	@Override
	public void populate(POModel model, Entity entity) {
		POInfo poInfo = model.getPOInfo();

		for (int index = 0; index < model.get_ColumnCount(); index++) {

			// int displayType = poInfo.getColumnDisplayType(index);
			String columName = poInfo.getColumnName(index);
			Object pValue = model.getValueOfColumn(columName);

			entity.addProperty(new Property(null, columName, ValueType.PRIMITIVE, pValue));

		}		
	}
	

}
