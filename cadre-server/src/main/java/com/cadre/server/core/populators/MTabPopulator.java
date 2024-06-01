package com.cadre.server.core.populators;

import javax.enterprise.inject.spi.CDI;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import com.cadre.server.core.annotation.CustomPopulator;
import com.cadre.server.core.converters.Populator;
import com.cadre.server.core.entity.MColumn;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.service.ModelService;

@CustomPopulator(MTabPopulator.TABLENAME_AD_TAB)
public class MTabPopulator implements Populator<POModel, Entity> {

	protected static final String TABLENAME_AD_TAB = "AD_Tab";
	private static final String PROPERTY_PARENT_COLUMN_NAME = "Parent_ColumnName";
	private static final String COLUMNNAME_PARENT_COLUMN_ID = "Parent_Column_ID";

	private ModelService modelService;
	
	
	public MTabPopulator() { init();	}
	
	private void init() {
		modelService =  CDI.current().select(ModelService.class).get();
		
	}

	@Override
	public void populate(POModel model, Entity entity) {
		POInfo poInfo = model.getPOInfo();

		for (int index = 0; index < model.get_ColumnCount(); index++) {

			// int displayType = poInfo.getColumnDisplayType(index);
			String columName = poInfo.getColumnName(index);
			Object pValue = model.getValueOfColumn(columName);

			entity.addProperty(new Property(null, columName, ValueType.PRIMITIVE, pValue));

			if (columName.equalsIgnoreCase(COLUMNNAME_PARENT_COLUMN_ID)) {
				if (pValue != null) {
					MColumn column = modelService.getPO(model.get_TrxName(), MColumn.TABLE_NAME, pValue);
					entity.addProperty(
							new Property(null, PROPERTY_PARENT_COLUMN_NAME, ValueType.PRIMITIVE, column.getColumnName()));

				}
			}
		}		
	}
	

}
