package com.cadre.server.core.populators;

import javax.enterprise.inject.spi.CDI;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;

import com.cadre.server.core.annotation.CustomPopulator;
import com.cadre.server.core.converters.Populator;
import com.cadre.server.core.entity.MProcess;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.service.ModelService;

@CustomPopulator(MToolBarButtonPopulator.TABLENAME)
public class MToolBarButtonPopulator implements Populator<POModel, Entity> {

	protected static final String TABLENAME = "AD_ToolBarButton";
	
	private ModelService modelService;
	
	
	public MToolBarButtonPopulator() { init();	}
	
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

			// Add column in ad_field to facilitate in the UI
			if (pValue != null) {
				if (columName.equals(MProcess.COLUMNNAME_AD_Process_ID)) {
					MProcess p = modelService.getPO(model.get_TrxName(), MProcess.TABLE_NAME, pValue);
					entity.addProperty(new Property(null,"ProcessValue", ValueType.PRIMITIVE,p.getValue()));
					
				}
			}
		}		
	}
	

}
