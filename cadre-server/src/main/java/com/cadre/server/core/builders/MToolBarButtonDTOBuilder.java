package com.cadre.server.core.builders;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;

import com.cadre.server.core.annotation.CustomBuilder;

@CustomBuilder(MToolBarButtonDTOBuilder.TABLENAME_AD_FIELD)
public class MToolBarButtonDTOBuilder extends AbsctractPODTOBuilder {

	protected static final String TABLENAME_AD_FIELD = "AD_ToolBarButton";

	@Override
	protected void addingCustomProperties() {

		final CsdlProperty processValueProperty = new CsdlProperty()
				.setName("ProcessValue")
				.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
		properties.add(processValueProperty);

	}

}
