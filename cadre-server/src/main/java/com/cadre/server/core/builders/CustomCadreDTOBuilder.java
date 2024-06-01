package com.cadre.server.core.builders;

import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;

public interface CustomCadreDTOBuilder {

	
	void build(String entityTypeName, CsdlEntityType entityType);

}
