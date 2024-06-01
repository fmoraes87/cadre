package com.cadre.server.core.validator;

import java.util.List;
import java.util.stream.IntStream;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.entity.validation.ModelChangeType;
import com.cadre.server.core.entity.validation.ModelValidationException;
import com.cadre.server.core.entity.validation.ModelValidator;
import com.cadre.server.core.persistence.exception.DBNoResultException;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.service.ModelService;

public abstract class AbstractModelValidator<T extends POModel> implements ModelValidator<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelValidator.class);

	
	protected ModelService modelService;
	
	public AbstractModelValidator() {
		modelService = CDI.current().select(ModelService.class).get();
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void modelChange(String trxName, POModel po, ModelChangeType changeType) {
		try {
			if (po!=null) {
				switch (changeType) {
				case TYPE_NEW:
				case TYPE_CHANGE:
					beforeSave(trxName,(T) po);
					break;
				case AFTER_NEW:
					afterNew(trxName, (T) po);
					break;
				case AFTER_CHANGE:
					afterSave(trxName,(T) po);
					break;
				case TYPE_DELETE:
					beforeDelete(trxName, (T) po);
					break;
				default:
					break;
				}
			}
			
		} catch (ClassCastException cce) {
			LOGGER.error("modelChange(changeType=" +changeType + ")", cce);

		    return;
		}
		
	}

	protected void afterNew(String trxName, T po) {};
	
	protected void afterSave(String trxName, T po) {};

	protected void beforeSave(String trxName, T po) {};
	
	protected void beforeDelete(String trxName, T po) {};

	/**
	 * Validate Unique constraint
	 * @param trxName
	 * @param newRecord
	 * @param tableName
	 * @param columnName
	 * @param value
	 * @param errorMsg
	 */
	protected void validateUnique(String trxName, boolean newRecord, String tableName, String columnName, Object value, String errorMsg) {
		validateUnique(trxName, newRecord, tableName, ArrayUtils.toArray(columnName), ArrayUtils.toArray(value), errorMsg);
	}
	
	/**
	 * Validate Unique Constraint
	 * @param trxName
	 * @param newRecord
	 * @param tableName
	 * @param columnName
	 * @param value
	 * @param errorMsg
	 */
	protected void validateUnique(String trxName, boolean newRecord, String tableName, String [] columnName, Object [] value, String errorMsg) {

		if (ArrayUtils.isNotEmpty(columnName) 
				&& ArrayUtils.isNotEmpty(value)
				&& ArrayUtils.isSameLength(columnName, value)) {
			
			final JDBCQueryImpl.Builder queryBuilder = new JDBCQueryImpl.Builder(tableName);

			IntStream.range(0, columnName.length)
				.forEach( i -> queryBuilder.and(GenericCondition.equals(columnName[i],value[i])));
					
			SearchResult<T> search 	= modelService.search(trxName, queryBuilder.build());
			
			boolean throwException = false;
			List<T> col= search.getResultList(throwException);
			if (CollectionUtils.isNotEmpty(col)  
					&& ((newRecord && col.size() > 0) || (!newRecord && col.size() > 1))
					) {
				throw new ModelValidationException(errorMsg);
			}
		}else {
			throw new IllegalArgumentException("Invalid Params");
		}
		
	}
	

	/**
	 * Validate Foreing Reference
	 * @param trxName
	 * @param tableName
	 * @param columnName
	 * @param value
	 * @param errorMsg
	 */
	protected void validateReference(String trxName, String tableName, String columnName, Object value,String errorMsg) {

		SearchResult<T> search = modelService.search(trxName,
				new JDBCQueryImpl.Builder(tableName)
					.and(GenericCondition.equals(columnName,value)).build());
		try {
			search.getSingleResult();			
		}catch (DBNoResultException ex) {
			LOGGER.error("validateReference(tableName=" +tableName +", coluName="+ columnName+ ",value="+ value+")", ex);
			throw new ModelValidationException(errorMsg);
		}

		
	}

	
}
