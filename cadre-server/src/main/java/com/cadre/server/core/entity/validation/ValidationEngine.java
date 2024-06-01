package com.cadre.server.core.entity.validation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.DynamicValidator;
import com.cadre.server.core.boundary.CadreFactory;
import com.cadre.server.core.boundary.ValidatorNameLiteral;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.event.CEvent;
import com.cadre.server.core.event.CEventManager;
import com.cadre.server.core.validator.AbstractModelValidator;

@Singleton
@SuppressWarnings({ "unchecked", "rawtypes" })
public class  ValidationEngine implements CadreFactory<String,ModelValidator> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationEngine.class);

	
	/**	Model Change Listeners			*/
	private Map<String,List<ModelValidator>>	_modelChangeListeners = new Hashtable<>();

	@Inject @Any
	private Instance<AbstractModelValidator<?>> validators;
	
	/**************************************************************************
	 * 	Constructor.
	 * 	Creates Model Validators
	 */
	public ValidationEngine () {
		
	}


	public void loadValidatorClass(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		//
		Class<?> clazz = Class.forName(className);
		DynamicValidator annotation = clazz.getAnnotation(DynamicValidator.class);
		if (annotation!=null) {
    		String key = annotation.value();
			ModelValidator validator = (ModelValidator) clazz.newInstance();
			load(key,validator);
			
		}

	}
	
	@Override
	public void load(String key, ModelValidator validator){

		List<ModelValidator> list = getDynamicValidators(key);
		if (list == null)
		{
			list = new ArrayList<ModelValidator>();
			list.add(validator);
			_modelChangeListeners.put(key, list);
		}
		else {
			list.add(validator);			
		}
		
	}
	
	public void fireBeforeAccessData(String tableName) {
		List<ModelValidator> list = getDynamicValidators(tableName);
		if (list != null)
		{
			for (ModelValidator validator: list) {
				 validator.beforeAccessData(tableName);
			}
		}

		Instance<AbstractModelValidator<?>> staticValidators = getStaticValidators(tableName);
		if (null!=staticValidators) {
			for (ModelValidator validator: staticValidators) {
				 validator.beforeAccessData(tableName);
			}
		}
		
		
	}


	private List<ModelValidator> getDynamicValidators(String tableName) {
		
		List<ModelValidator> currentValidators = _modelChangeListeners.get(tableName);	
		return currentValidators;
	}
	

	public <T extends POModel>void fireBeforeAccessData(T po) {
		if (po == null ) {
			return;			
		}
		final String tableName = po.get_TableName();

		
		List<ModelValidator> list = getDynamicValidators(tableName);
		if (list != null)
		{
			for (ModelValidator validator: list) {
				 validator.beforeAccessModel(po);
			}
		}
		
		Instance<AbstractModelValidator<?>> staticValidators = getStaticValidators(tableName);
		if (null!=staticValidators) {
			for (ModelValidator validator: staticValidators) {
				 validator.beforeAccessModel(po);
			}
		}
		
	}
	
	

	/**
	 * 	Fire Model Event.
	 * 	Call Model Event method of added validators
	 *	@param po objects
	 *	@param type ModelValidator.TYPE_*
	 *	@return error message or NULL for no veto
	 */
	public void fireModelChange (POModel po, ModelChangeType changeType)
	{
		if (po == null ) {
			return;			
		}
		final String tableName = po.get_TableName();
		
		List<ModelValidator> list = getDynamicValidators(tableName);
		if (list != null)
		{
			for (ModelValidator validator: list) {
				 validator.modelChange(po.get_TrxName(),po, changeType);
			}
		}
		
		Instance<AbstractModelValidator<?>> staticValidators = getStaticValidators(tableName);
		if (null!=staticValidators) {
			for (ModelValidator validator: staticValidators) {
				 validator.modelChange(po.get_TrxName(),po, changeType);
			}
		}
		
		
		//For communication between extensions
		CEvent event = CEventManager.newEvent(changeType.name(),po.get_TableName(), po);
		CEventManager.getInstance().postEvent(event);
		
	}


	private Instance<AbstractModelValidator<?>> getStaticValidators(final String tableName) {
		Instance<AbstractModelValidator<?>> staticValidators 
		=  this.validators.select(new TypeLiteral<AbstractModelValidator<?>>(){},new ValidatorNameLiteral(tableName));
		return staticValidators;
	}
	
	/**************************************************************************
	 * 	Add Model Handler Listener
	 *	@param tableName table name
	 *	@param listener listener
	 */
	public void addModelChange (String tableName, ModelValidator listener)
	{
		if (tableName == null || listener == null) {
			return;			
		}

		List<ModelValidator> list = getDynamicValidators(tableName);
		if (list == null)
		{
			list = new ArrayList<ModelValidator>();
			list.add(listener);
			_modelChangeListeners.put(tableName, list);
		}
		else {
			list.add(listener);			
		}
	}
	
	/**
	 * 	Remove Model Change Listener
	 *	@param tableName table name
	 *	@param listener listener
	 */
	public void removeModelChange (String tableName, ModelValidator listener)
	{
		if (tableName == null || listener == null) {
			return;			
		}

		List<ModelValidator> list = getDynamicValidators(tableName);
		if (list == null)
			return;
		list.remove(listener);
		if (list.size() == 0) {
			_modelChangeListeners.remove(tableName);			
		}
	}	//	removeModelValidator






}
