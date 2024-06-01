package com.cadre.server.core.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;

import com.cadre.server.core.entity.MColumn;
import com.cadre.server.core.entity.MTable;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.ADReferenceODataConverter;
import com.cadre.server.core.util.DisplayType;

public abstract class AbsctractPODTOBuilder implements CustomCadreDTOBuilder {

	// Create list of properties
	protected final CsdlPropertyRef propertyRef = new CsdlPropertyRef();
	protected final List<CsdlProperty> properties = new ArrayList<>();
	protected final List<CsdlNavigationProperty> navPropList = new ArrayList<>();
	
	@Override
	public void build(String entityTypeName, CsdlEntityType entityType) {
		
		if (null==entityType) {
			entityType = new CsdlEntityType();
		}

		ModelService s = CDI.current().select(ModelService.class).get();
		MTable table = s.getPO(null, MTable.TABLE_NAME, MTable.COLUMNNAME_TableName, entityTypeName);
		entityType.setName(table.getTableName());

		SearchResult<MColumn> columnsSearch = s.search(null, new JDBCQueryImpl.Builder(MColumn.TABLE_NAME)
				.and(GenericCondition.equals(MColumn.COLUMNNAME_AD_Table_ID, table.getAD_Table_ID())).build());



		List<MColumn> tableColumns = columnsSearch.getResultList();
		tableColumns.forEach(column -> {
			parseColumnToProperties(column);
		});
		
		addingCustomProperties();

		// configure EntityType
		entityType.setKey(Collections.singletonList(propertyRef));
		entityType.setProperties(properties);
		entityType.setNavigationProperties(navPropList);
	
	}

	protected void addingCustomProperties() {
		// TODO Auto-generated method stub
		
	}

	protected void parseColumnToProperties(MColumn column) {
		final CsdlProperty property = createProperty(column);

		properties.add(property);

		if (column.isKey()) {
			propertyRef.setName(column.getColumnName());
		}		
	}

	protected CsdlProperty createProperty(MColumn column) {
		final EdmPrimitiveTypeKind type = ADReferenceODataConverter.get( column.getAD_Reference_ID());

		final CsdlProperty property = new CsdlProperty()
				 .setName(column.getColumnName())
				.setType(type.getFullQualifiedName());

		if (type.equals(EdmPrimitiveTypeKind.Decimal)) {

			int scale = DisplayType.getDefaultPrecision(column.getAD_Reference_ID());
			property.setScale(18 + scale);
		}
		return property;
	}

}
