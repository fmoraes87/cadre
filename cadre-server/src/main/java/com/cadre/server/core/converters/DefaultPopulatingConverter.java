package com.cadre.server.core.converters;


import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class DefaultPopulatingConverter<SOURCE,TARGET> extends AbstractConverter<SOURCE, TARGET> implements PopulatorList<SOURCE, TARGET>  {

	private List<Populator<SOURCE,TARGET>> populators;

	@Override
	public void populate(SOURCE source, TARGET target) {
		List<Populator<SOURCE,TARGET>> list = getPopulators();
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		
		list.stream().forEach(p -> {
			if (p!=null) {
				p.populate(source, target);
			}
			
		});
	}

	@Override
	public List<Populator<SOURCE, TARGET>> getPopulators() {
		return populators;
	}

	@Override
	public void setPopulators(List<Populator<SOURCE, TARGET>> populators) {
		this.populators= populators;
	}
	
	
	
}