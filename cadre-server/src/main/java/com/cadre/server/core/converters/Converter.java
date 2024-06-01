package com.cadre.server.core.converters;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public interface Converter<SOURCE, TARGET> {

	TARGET convert(SOURCE source);

	TARGET convert(SOURCE source, TARGET target);

	default List<TARGET> convertAll(Collection<? extends SOURCE> sources) {
		if (CollectionUtils.isEmpty(sources)) {
			return Collections.emptyList();
		} else {
			List<TARGET> results = new ArrayList<>(sources.size());
			sources.stream().forEach(source -> {
				results.add(this.convert(source));
			});

			return results;
		}
	}

}
