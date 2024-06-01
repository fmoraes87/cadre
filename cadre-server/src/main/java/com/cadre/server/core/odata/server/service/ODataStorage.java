package com.cadre.server.core.odata.server.service;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.dto.RequestDataAccess;
import com.cadre.server.core.entity.DatabaseOperation;
import com.cadre.server.core.entity.ResourceType;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.service.CadreServiceFacade;
import com.cadre.server.core.service.DataAccessService;

@Singleton
public class ODataStorage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ODataStorage.class);

	private DataAccessService accessService;

	@Inject
	private CadreServiceFacade provider;

	private ODataStorage() {
		this.accessService = DynamicServiceResolver.locate(DataAccessService.class);

	}

	public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet) throws ODataApplicationException {
		return readEntitySetData(edmEntitySet,null,null,null,null,null);
	}
	
	
	/**
	 * Helper method for providing data
	 * 
	 * @param edmEntitySet for which the data is requested
	 * @param uriInfo
	 * @return data of requested entity set
	 * @throws ODataApplicationException
	 */
	public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet, FilterOption filterOption,  
					OrderByOption orderByOption, SkipOption skipOption, TopOption topOption, CountOption countOption )
			throws ODataApplicationException {
		

		RequestDataAccess request = new RequestDataAccess(ResourceType.TABLE_COLUMN, edmEntitySet.getName(),DatabaseOperation.READ);
				
		try {
			if (accessService.isClientAllowed(CadreEnv.getTrxName(), request)) {
											
				EntityCollection entityCollection = provider.readEntitySetData(edmEntitySet,filterOption,orderByOption,skipOption,topOption,countOption);
				
				return entityCollection;

			} else {
				throw new ODataApplicationException(DataAccessService.CLIENT_NOT_AUTHORIZED,
						HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ENGLISH);
			}
		} catch (CadreException ex) {
			LOGGER.error("readEntitySetData("+edmEntitySet.getName() + ")", ex);

			throw handleException(ex);
		}

	}


	public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
			throws ODataApplicationException {
		
		RequestDataAccess request = new RequestDataAccess(ResourceType.TABLE_COLUMN, edmEntitySet.getName(),DatabaseOperation.READ);

		try {
			if (accessService.isClientAllowed(CadreEnv.getTrxName(), request)) {
					
				Entity entity = provider.readEntityData(edmEntitySet, keyParams);
				
				if (entity==null) {
					throw new ODataApplicationException("@NoEntityFound@", HttpStatusCode.NOT_FOUND.getStatusCode(),
							Locale.ENGLISH);
				}
				return entity;
	
			} else {
				throw new ODataApplicationException(DataAccessService.CLIENT_NOT_AUTHORIZED,
						HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ENGLISH);
			}
		} catch (CadreException ex) {
			LOGGER.error("readEntityData("+edmEntitySet.getName() + ")", ex);

			throw handleException(ex);
		}

	}

	/**
	 * 
	 * @param edmEntityType
	 * @param keyParams
	 * @param updateEntity
	 * @throws ODataApplicationException
	 */
	public Entity updateEntityData(EdmEntityType edmEntityType, List<UriParameter> keyParams, Entity updateEntity) throws ODataApplicationException {
				
		RequestDataAccess request = new RequestDataAccess(ResourceType.TABLE_COLUMN, edmEntityType.getName(), DatabaseOperation.WRITE);

		try {
			if (accessService.isClientAllowed(CadreEnv.getTrxName(), request)) {
				
				return provider.updateEntityData(edmEntityType,keyParams,updateEntity);

			} else {
				throw new ODataApplicationException(DataAccessService.CLIENT_NOT_AUTHORIZED,
						HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ENGLISH);
			}
		} catch (Exception ex) {
			LOGGER.error("updateEntityData("+edmEntityType.getName() + ")", ex);

			throw handleException(ex);
		}

	}


	/**
	 * Create Entity
	 * 
	 * @param edmEntityType
	 * @param entityToCreate
	 * @throws ODataApplicationException
	 */
	public Entity createEntityData(EdmEntityType edmEntityType, Entity entityToCreate) throws ODataApplicationException {

		RequestDataAccess request = new RequestDataAccess(ResourceType.TABLE_COLUMN,  edmEntityType.getName(), DatabaseOperation.WRITE);

		try {
			if (accessService.isClientAllowed(CadreEnv.getTrxName(), request)) {
				return provider.createEntityData(edmEntityType,entityToCreate);

			} else {
				throw new ODataApplicationException(DataAccessService.CLIENT_NOT_AUTHORIZED,
						HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ENGLISH);
			}
		} catch (Exception ex) {
			LOGGER.error("createEntityData("+edmEntityType.getName() + ")", ex);

			throw handleException(ex);
		}

	}

	public void deleteEntityData(EdmEntityType edmEntityType, List<UriParameter> keyParams) throws ODataApplicationException {
		
		RequestDataAccess request = new RequestDataAccess(ResourceType.TABLE_COLUMN, edmEntityType.getName(), DatabaseOperation.WRITE);
		
		try {
			if (accessService.isClientAllowed(CadreEnv.getTrxName(),request)) {
				provider.deleteEntityData(edmEntityType, keyParams);
	
			} else {
				throw new ODataApplicationException(DataAccessService.CLIENT_NOT_AUTHORIZED,
						HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ENGLISH);
			}
		} catch (Exception ex) {
			LOGGER.error("deleteEntityData("+edmEntityType.getName() + ")", ex);

			throw handleException(ex);
		}
	}

	private ODataApplicationException handleException(Exception ex) {
	
		if (ex instanceof CadreException) {
			CadreException e = (CadreException) ex;
			return new ODataApplicationException(e.getCode(),e.getStatus(), Locale.ENGLISH);
		}else {
			return new ODataApplicationException(ex.getMessage(),
					HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
		}
	}



}
