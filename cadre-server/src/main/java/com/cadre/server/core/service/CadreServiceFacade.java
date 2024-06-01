package com.cadre.server.core.service;

import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

import com.cadre.server.core.boundary.ServiceProvider;

public interface CadreServiceFacade extends ServiceProvider {

	EntityCollection readEntitySetData(EdmEntitySet edmEntitySet, 
			FilterOption filterOption,  OrderByOption orderByOption, SkipOption skipOption, TopOption topOption, CountOption countOption  ) ;
	
	Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams);

	Entity updateEntityData(EdmEntityType edmEntityType, List<UriParameter> keyParams, Entity updateEntity);

	Entity createEntityData(EdmEntityType edmEntityType, Entity entityToCreate);

	void deleteEntityData(EdmEntityType edmEntityType, List<UriParameter> keyParams);

}
