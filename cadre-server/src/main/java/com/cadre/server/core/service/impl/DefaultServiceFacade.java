package com.cadre.server.core.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.converters.DefaultPopulatingConverter;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.entity.validation.ValidationEngine;
import com.cadre.server.core.persistence.query.FlexibleSearchQuery;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.populators.DefaultPopulatorProvider;
import com.cadre.server.core.service.CadreServiceFacade;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.ODataSQLUtils;
import com.cadre.server.core.util.SecurityUtils;

@Singleton
public class DefaultServiceFacade implements CadreServiceFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceFacade.class);

	@Inject
	private ModelService genericService;
	
	@Inject
	private ValidationEngine validationEngine;
	
	public DefaultServiceFacade() {
	}


	private Entity convertEntity(POModel model) {
		String tableName = model.get_TableName();
		DefaultPopulatingConverter<POModel,Entity> converter = new DefaultPopulatingConverter<>();
		converter.setTargetClass(Entity.class);
		converter.setPopulators(DefaultPopulatorProvider.get().getElements(tableName));
		
		
		return converter.convert(model);
	}

	/**
	 * Load mode from keys
	 * 
	 * @param keyParams
	 * @param tablename
	 * @param trxName
	 * @return
	 */
	private POModel getPOModelFrom(List<UriParameter> keyParams, final String tablename) {
		
		final FlexibleSearchQuery flxQuery = new FlexibleSearchQuery(tablename);
		
		StringBuilder whereClause = new StringBuilder("1=1");
		
		for (final UriParameter key : keyParams) {
			final String value = key.getAlias() == null ? key.getText() : ((Literal) key.getExpression()).getText();
			whereClause.append(" AND "  +key.getName()+ " = " +  value);
		}
		whereClause.append(SecurityUtils.getDynamicRules(tablename));
		flxQuery.setWhereClause(whereClause.toString());

		SearchResult<? extends POModel> searchPO = genericService.search(CadreEnv.getTrxName(), flxQuery);
		POModel result = searchPO.getSingleResult();
		return result;
	}

	@Override
	public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet, 
				FilterOption filterOption,  OrderByOption orderByOption, SkipOption skipOption, TopOption topOption, CountOption countOption  ) {

		EntityCollection entityCollection = new EntityCollection();
		
		validationEngine.fireBeforeAccessData(edmEntitySet.getName());


		final FlexibleSearchQuery flxQuery = new FlexibleSearchQuery(edmEntitySet.getName());
		POInfo poInfo = genericService.getPOInfo(edmEntitySet.getName());
		StringBuilder whereClause = new StringBuilder(ODataSQLUtils.getWhereClause(poInfo, filterOption));
		if (poInfo.getTableName().equals("AD_TreeNode")) {
			whereClause.append(SecurityUtils.getMenuAccessSQL());
		}
		whereClause.append(SecurityUtils.getDynamicRules(edmEntitySet.getName()));
		
		flxQuery.setWhereClause(whereClause.toString());
		flxQuery.setOrderBy(ODataSQLUtils.getOrderBy(poInfo, orderByOption));
		flxQuery.setTop(ODataSQLUtils.getTop(topOption));
		flxQuery.setSkip(ODataSQLUtils.getSkip(skipOption));	

		List<? extends POModel> models = genericService.search(CadreEnv.getTrxName(), flxQuery).getResultList(false);

		if (CollectionUtils.isNotEmpty(models)) {
			models = models.stream().filter(po -> SecurityUtils.canView(po)).collect(Collectors.toList());
			
			if (countOption != null && countOption.getValue()) {
				entityCollection.setCount(models.size());
			} else {
				models.forEach(source -> {
					entityCollection.getEntities().add(convertEntity(source));
				});
			}

		} else {
			if (countOption != null && countOption.getValue()) {
				entityCollection.setCount(0);
			}
		}

		return entityCollection;
	}


	@Override
	public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) {

		if (keyParams != null) {
			
			POModel poModel = getPOModelFrom(keyParams, edmEntitySet.getName());
			if (poModel != null && SecurityUtils.canView(poModel)) {
				validationEngine.fireBeforeAccessData(poModel);
				return convertEntity(poModel);
			} else {
				return null;
			}

		}

		throw new IllegalArgumentException("keyParams is null");
	}

	@Override
	public Entity updateEntityData(EdmEntityType edmEntityType, List<UriParameter> keyParams, Entity updateEntity) {
		if (keyParams != null) {

			POModel poModel = getPOModelFrom(keyParams, edmEntityType.getName());
			if (SecurityUtils.canUpdate(poModel)) {
				POInfo poInfo = poModel.getPOInfo();

				List<Property> properties = updateEntity.getProperties();
				for (Property existingProp : properties) {

					String propName = existingProp.getName();
					int index = poInfo.getColumnIndex(propName);
					if (index >= 0) {
						if (poInfo.getPOInfoColumn(propName).isUpdatable) {
							if (existingProp.getValue() instanceof GregorianCalendar) {
								GregorianCalendar propValue= (GregorianCalendar) existingProp.getValue();
								poModel.setValueOfColumn(existingProp.getName(), new Timestamp(propValue.getTimeInMillis()));							
							}else {
								poModel.setValueOfColumn(existingProp.getName(), existingProp.getValue());							
								
							}						
						}else {
							//TODO LOG warning
						}
					}else {
						//TODO LOG warning
					}
				}

				genericService.save(poModel);

				return convertEntity(poModel);
			} else {
				throw new SecurityException();

			}

		}

		throw new IllegalArgumentException("keyParams == null");

	}

	@Override
	public Entity createEntityData(EdmEntityType edmEntityType, Entity entityToCreate) {

		POModel poModel = genericService.createPO(CadreEnv.getTrxName(), edmEntityType.getName());
		POInfo poInfo = poModel.getPOInfo();

		for (Property prop : entityToCreate.getProperties()) {
			// Ignore ID
			if (prop.getName().equals(edmEntityType.getName() + "_ID")) {
				continue;
			}
			
			int index = poInfo.getColumnIndex(prop.getName());
			if (index >= 0) {
				if (poInfo.getPOInfoColumn(prop.getName()).isUpdatable) {
					if (prop.getValue() instanceof GregorianCalendar) {
						GregorianCalendar propValue= (GregorianCalendar) prop.getValue();
						poModel.setValueOfColumn(prop.getName(), new Date(propValue.getTimeInMillis()));	
					}else {
						poModel.setValueOfColumn(prop.getName(), prop.getValue());							
						
					}
				}else {
					LOGGER.warn("Column is not updatable: " + prop.getName());
				}
			}else {
				LOGGER.warn("Column not found: " + prop.getName());
			}

		}

		genericService.save(poModel);

		return convertEntity(poModel);
	}

	@Override
	public void deleteEntityData(EdmEntityType edmEntityType, List<UriParameter> keyParams) {
		if (keyParams != null) {

			POModel poModel = getPOModelFrom(keyParams, edmEntityType.getName());

			if (SecurityUtils.canUpdate(poModel)) {
				genericService.delete(poModel);
			} else {
				throw new SecurityException();
			}

			return;
		}

		throw new IllegalArgumentException("keyParams == null");

	}
	

}
