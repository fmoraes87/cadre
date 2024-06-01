package com.cadre.server.core.odata.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.builders.DefaultDTOBuilderProvider;
import com.cadre.server.core.cache.CCache;
import com.cadre.server.core.entity.MProcess;
import com.cadre.server.core.entity.MProcessPara;
import com.cadre.server.core.entity.MTable;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.odata.server.action.ODataActionProcessor;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchQuery;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.ADReferenceODataConverter;

/**
 * Custom EDM Provider
 * 
 * @author Fernando Moraes (m87.fernando@gmail.com)
 */
@Singleton
public class ODataEdmProvider extends CsdlAbstractEdmProvider {


	private static final Logger LOGGER = LoggerFactory.getLogger(ODataEdmProvider.class);

	// Service Namespace
	public static final String NAMESPACE = "Cadre";

	// EDM Container
	public static final String CONTAINER_NAME = "Cadre";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_TABLE_NAME = "AD_Table";
	public static final FullQualifiedName ET_TABLE_FQN = new FullQualifiedName(NAMESPACE, ET_TABLE_NAME);

	@Inject
	private ModelService modelService;

	public ODataEdmProvider() {

	}

	/**
	 * Cache CsdlEntityType
	 */
	private static CCache<String, CsdlEntityType> entityTypeCache = new CCache<>("CsdlEntityType", 50);

	/**
	 * Cache CsdlEntitySet
	 */
	private static CCache<String, CsdlEntitySet> entitySetCache = new CCache<>("CsdlEntitySet", 50);

	@Override
	public List<CsdlSchema> getSchemas() {

		// create Schema
		CsdlSchema schema = new CsdlSchema();
		schema.setNamespace(NAMESPACE);

		// add EntityContainer
		schema.setEntityContainer(getEntityContainer());

		// add actions
		List<CsdlAction> actions = new ArrayList<CsdlAction>();
		//actions.addAll(getActions(ACTION_MODULO_XPTO_FQN));
		schema.setActions(actions);
		
		// add functions
		List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
		//functions.addAll(getFunctions(FUNCTION_COUNT_CATEGORIES_FQN));
		schema.setFunctions(functions);
		
		// finally
		List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
		schemas.add(schema);

		return schemas;
	}

	@Override
	public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {

		// this method is called for one of the EntityTypes that are configured in the
		// Schema
		// Verify cache
		CsdlEntityType entityType = entityTypeCache.get(entityTypeName.getName());

		if (entityType != null) {
			return entityType;
		} else {

			try {
				entityType = DefaultDTOBuilderProvider.get().build(entityTypeName.getName());
				entityTypeCache.put(entityType.getName(), entityType);
			} catch (CadreException ex) {
				LOGGER.error("getEntityType(" + entityTypeName.getName() + ")", ex);

				throw new ODataApplicationException(ex.getMessage(), HttpStatusCode.NOT_FOUND.getStatusCode(),
						Locale.ENGLISH);
			}

			return entityType;

		}

	}

	@Override
	public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {

		if (entityContainer.equals(CONTAINER)  && !entitySetName.startsWith(ODataActionProcessor.ACTION_PREFIX_NAME)) {
			// Verify cache
			CsdlEntitySet entitySet = entitySetCache.get(entitySetName);

			if (entitySet != null) {
				return entitySet;
			} else {
				try {
					MTable table = modelService.getPO(null, MTable.TABLE_NAME, MTable.COLUMNNAME_TableName,
							entitySetName);

					entitySet = new CsdlEntitySet();
					entitySet.setName(table.getTableName());
					entitySet.setType(new FullQualifiedName(ODataEdmProvider.NAMESPACE, table.getTableName()));
					// entitySet.setNavigationPropertyBindings(navPropBindingList);

					entitySetCache.put(table.getTableName(), entitySet);
					return entitySet;

				} catch (CadreException ex) {
					LOGGER.error("getEntitySet(" + CONTAINER + "," + entitySetName + ")", ex);
					throw new ODataApplicationException(ex.getMessage(), HttpStatusCode.NOT_FOUND.getStatusCode(),	Locale.ENGLISH);
				}
			}

		}

		return null;

	}

	@Override
	public CsdlEntityContainer getEntityContainer() {

		// create EntitySets
		// List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
		// entitySets.add(getEntitySet(CONTAINER, ES_TABLES_NAME));

		// create EntityContainer
		CsdlEntityContainer entityContainer = new CsdlEntityContainer();
		entityContainer.setName(CONTAINER_NAME);
		// entityContainer.setEntitySets(entitySets);

		return entityContainer;
	}

	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {

		// This method is invoked when displaying the service document at e.g.
		// http://localhost:8080/DemoService/DemoService.svc
		if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
			CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
			entityContainerInfo.setContainerName(CONTAINER);
			return entityContainerInfo;
		}

		return null;
	}

	@Override
	public List<CsdlAction> getActions(final FullQualifiedName actionName) {

		// It is allowed to overload actions, so we have to provide a list of Actions
		// for each action name
		final List<CsdlAction> actions = new ArrayList<CsdlAction>();
		
		
		String adProcessValue =actionName.getName().substring(ODataActionProcessor.ACTION_PREFIX_NAME.length());

		
		MProcess p = modelService.getPO(null, MProcess.TABLE_NAME, MProcess.COLUMNNAME_Value,adProcessValue);

		
		// Create parameters
		final List<CsdlParameter> parameters = new ArrayList<CsdlParameter>();
		
		SearchQuery queryParams = new JDBCQueryImpl.Builder(MProcessPara.TABLE_NAME)
				.and(GenericCondition.equals(MProcessPara.COLUMNNAME_AD_Process_ID,p.getAD_Process_ID()))
				.and(GenericCondition.equals(POModel.COLUMNNAME_IsActive,true))
				.build();

		SearchResult<MProcessPara> userResult = modelService.search(null, queryParams);
		userResult.getResultList(false).forEach( param-> {
			final CsdlParameter parameter = new CsdlParameter();
			parameter.setName(param.getColumnName());
			
			final EdmPrimitiveTypeKind type = ADReferenceODataConverter.get( param.getAD_Reference_ID());
			parameter.setType(type.getFullQualifiedName());
			parameters.add(parameter);
			
		});
		// Create the Csdl Action
		final CsdlAction action = new CsdlAction();
		action.setName(ODataActionProcessor.ACTION_PREFIX_NAME+adProcessValue);
		action.setParameters(parameters);

		final CsdlReturnType returnType = new CsdlReturnType();
		returnType.setCollection(false);
		returnType.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

		action.setReturnType(returnType);

		actions.add(action);

		return actions;
	}

	@Override
	public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName) throws ODataApplicationException {

		if (entityContainer.equals(CONTAINER) && actionImportName.startsWith(ODataActionProcessor.ACTION_PREFIX_NAME)) {

			try {
				String adProcessValue = actionImportName.substring(ODataActionProcessor.ACTION_PREFIX_NAME.length());

				MProcess process = modelService.getPO(null, MProcess.TABLE_NAME,MProcess.COLUMNNAME_Value,adProcessValue);
				
				FullQualifiedName processFQN = new FullQualifiedName(NAMESPACE, actionImportName);

				return new CsdlActionImport().setName(actionImportName).setAction(processFQN);

			} catch (CadreException ex) {
				LOGGER.error("getEntitySet(" + CONTAINER + "," + actionImportName + ")", ex);
				throw new ODataApplicationException(ex.getMessage(), HttpStatusCode.NOT_FOUND.getStatusCode(),	Locale.ENGLISH);
			}

		}

		return null;
	}

}
