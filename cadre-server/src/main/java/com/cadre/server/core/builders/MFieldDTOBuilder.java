package com.cadre.server.core.builders;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;

import com.cadre.server.core.annotation.CustomBuilder;
import com.cadre.server.core.entity.MColumn;

@CustomBuilder(MFieldDTOBuilder.TABLENAME_AD_FIELD)
public class MFieldDTOBuilder extends AbsctractPODTOBuilder {

	protected static final String TABLENAME_AD_FIELD = "AD_Field";

	@Override
	protected void addingCustomProperties() {

		// Add column in ad_field to facilitate in the UI
		final CsdlProperty columnNameProperty = new CsdlProperty().setName(MColumn.COLUMNNAME_ColumnName)
				.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		properties.add(columnNameProperty);

		final CsdlProperty referenceIdProperty = new CsdlProperty().setName(MColumn.COLUMNNAME_AD_Reference_ID)
				.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		properties.add(referenceIdProperty);

		final CsdlProperty referenceValueIdProperty = new CsdlProperty()
				.setName(MColumn.COLUMNNAME_AD_Reference_Value_ID)
				.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
		properties.add(referenceValueIdProperty);
		
		final CsdlProperty processValueProperty = new CsdlProperty()
				.setName("ProcessValue")
				.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		properties.add(processValueProperty);

	}

}
