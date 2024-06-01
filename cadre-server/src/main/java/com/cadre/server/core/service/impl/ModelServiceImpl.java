package com.cadre.server.core.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.entity.validation.ModelChangeType;
import com.cadre.server.core.entity.validation.ValidationEngine;
import com.cadre.server.core.persistence.GenericDAO;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchQuery;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.resolver.ModelResolver;
import com.cadre.server.core.service.ModelService;

@Singleton
public class ModelServiceImpl implements ModelService {


	public static final String LOCAL_TRX_PREFIX = "PO";

	@Inject
	private GenericDAO genericDAO;
	
	@Inject
	private ValidationEngine validationEngine;

	public ModelServiceImpl() {
		
	}

	@Override
	public POInfo getPOInfo(String tableName) {
		return genericDAO.getPOInfo(tableName);
	}

	@Override
	public <T extends POModel> SearchResult<T> search(final String trxName, final SearchQuery searchQuery) {
		
		return genericDAO.getModels(trxName, searchQuery);			
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends POModel> T createPO(String trxName,String tableName) {
		T model = (T) ModelResolver.get().resolve(tableName);
		if (model.getPOInfo()==null) {
			model.setPoInfo(genericDAO.getPOInfo(tableName));				
		}
		model.set_TrxName(trxName);
		return model;
	}
	
	@Override
	public <T extends POModel> T getPO(String trxName, String tableName, Object id) {
		return getPO(trxName, tableName, tableName + "_ID", id);			

	}

	@Override
	public <T extends POModel> T getPO(String trxName, String tablename, String columnName, Object property) {
		
		
		SearchResult<T> columnsSearch = search(trxName,
				new JDBCQueryImpl.Builder(tablename).and(GenericCondition.equals(columnName, property)).build());

		T t = columnsSearch.getSingleResult(true);
		
		return t;

	}

	/**
	 * 	Called before Save for Pre-Save Operation
	 * 	@param newRecord new record
	 *	@return true if record can be saved
	 */
	protected boolean beforeSave(boolean newRecord)
	{

		return true;
	}	
	
	
	/**
	 * 	Called after Save for Post-Save Operation
	 * 	@param newRecord new record
	 */
	protected void afterSave (boolean newRecord)
	{

	}	//	afterSave
	
	
	
	@Override
	public <T extends POModel> void save(T po)  {
		boolean newRecord = po.isNew();
		if (beforeSave(newRecord)) {
			if (newRecord || po.isChanged()) {
				
				if (po.isNew()) {
					po.setAD_Client_ID(CadreEnv.getAD_Client_ID());
					po.setAD_Org_ID(CadreEnv.getAD_Org_ID());
					po.setCreatedBy(CadreEnv.getAD_User_ID());
					po.setUpdatedBy(CadreEnv.getAD_User_ID());
					po.setUUColumn(UUID.randomUUID().toString());
					
				}else if (po.isChanged()) {
					po.setUpdatedBy(CadreEnv.getAD_User_ID());
					po.setValueOfColumn(POModel.COLUMNNAME_Updated, new Timestamp(System.currentTimeMillis()));
				}
				
				// Before Save
				// Call ModelValidators TYPE_NEW/TYPE_CHANGE
				validationEngine.fireModelChange(po, newRecord ? ModelChangeType.TYPE_NEW : ModelChangeType.TYPE_CHANGE);
				
				genericDAO.save(po);
				afterSave (newRecord);
				// After Save
				// Call ModelValidators TYPE_NEW/TYPE_CHANGE
				validationEngine.fireModelChange(po, newRecord ? ModelChangeType.AFTER_NEW : ModelChangeType.AFTER_CHANGE);		
				
				
				//For communication between extensions
//				String topic = newRecord ? IEventTopics.PO_POST_CREATE : IEventTopics.PO_POST_UPADTE;
//				Event event = EventManager.newEvent(topic, this);
//				EventManager.getInstance().postEvent(event);
				
			}
			
		}
	}
	
	/**
	 * 	Executed before Delete operation.
	 *	@return true if record can be deleted
	 */
	protected boolean beforeDelete ()
	{
		return true;
	} 	//	beforeDelete

	/**
	 * 	Executed after Delete operation.
	 * 	@param success true if record deleted
	 *	@return true if delete is a success
	 */
	protected void afterDelete () 	{} 	//	afterDelete


	@Override
	public <T extends POModel> void delete(T po) {

		if (beforeDelete()) {
			// Before Delete
			// Call ModelValidators TYPE_NEW/TYPE_CHANGE
			validationEngine.fireModelChange(po, ModelChangeType.TYPE_DELETE);
			
			genericDAO.delete(po, false);
			afterDelete ();
			// After Save
			// Call ModelValidators TYPE_NEW/TYPE_CHANGE
			validationEngine.fireModelChange(po, ModelChangeType.TYPE_AFTER_DELETE);
			
			//Event event = EventManager.newEvent(IEventTopics.PO_POST_DELETE, this);
			//EventManager.getInstance().postEvent(event);
		}


	}

	@Override
	public void deleteAll(String trxName, SearchQuery query) {
		Optional<List<? extends POModel>> result = Optional.ofNullable(search(trxName,query).getResultList(false));
		result.ifPresent(list -> list.stream().forEach(po -> delete(po)));
		
	}

}
