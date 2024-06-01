package com.cadre.server.core.populators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;

import com.cadre.server.core.boundary.CadreFactory;
import com.cadre.server.core.converters.Populator;
import com.cadre.server.core.entity.POModel;
import com.google.common.base.Optional;

public class DefaultPopulatorProvider implements CadreFactory<String,Populator<POModel, Entity>> {

	private static final List<Populator<POModel, Entity>> DEFAULT_PROVIDER = Arrays.asList(DefaultPOPopulator.get());
	/**	Populators			*/
	private Map<String,List<Populator<POModel, Entity>>>	_populators = new Hashtable<>();


	 // Inner class to provide instance of class 
	private static class Singleton {
		private static final DefaultPopulatorProvider INSTANCE = new DefaultPopulatorProvider();
	}

	public static DefaultPopulatorProvider get() {
		return Singleton.INSTANCE;
	}
	
	private DefaultPopulatorProvider() {}
	
	@Override
	public void load(String key, Populator<POModel, Entity> populator){

		List<Populator<POModel, Entity> > list = _populators.get(key);
		if (list == null)
		{
			list = new ArrayList<Populator<POModel, Entity>>();
			list.add(DefaultPOPopulator.get());
			list.add(populator);
			_populators.put(key, list);
		}
		else {
			list.add(populator);			
		}
		
	}
	
	@Override
	public List<Populator<POModel, Entity>> getElements(String key){
		Optional<List<Populator<POModel, Entity>>> populators = Optional.fromNullable(_populators.get(key));
		if (populators.isPresent()) {
			return populators.get();
		}else {
			return DEFAULT_PROVIDER;
		}
	}
	
}
