package com.cadre.server.core.builders;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;

import com.cadre.server.core.annotation.CustomBuilder;

@CustomBuilder(MTabDTOBuilder.TABLENAME_AD_FIELD)
public class MTabDTOBuilder extends AbsctractPODTOBuilder {

	protected static final String TABLENAME_AD_FIELD = "AD_Tab";

	@Override
	protected void addingCustomProperties() {

		final CsdlProperty columnNameProperty = new CsdlProperty().setName("Parent_ColumnName")
				.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		properties.add(columnNameProperty);

	}

}
