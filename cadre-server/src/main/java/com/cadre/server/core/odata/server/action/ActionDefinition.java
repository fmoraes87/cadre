package com.cadre.server.core.odata.server.action;

import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;

public interface ActionDefinition {


	CsdlActionImport getCsdlActionImport();
	
	CsdlAction getCsdlAction();

}
