package com.cadre.server.core.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class Converters {

	private Converters() {
		// prevent instance creation
	}

	public static <SOURCE, TARGET> List<TARGET> convertAll(final Collection<? extends SOURCE> sourceList,
			final Converter<SOURCE, TARGET> converter) {

		if (CollectionUtils.isEmpty(sourceList)) {
			return Collections.emptyList();
		}

		final List<TARGET> results = new ArrayList<TARGET>(sourceList.size());
		sourceList.stream().forEach(source -> {
			results.add(converter.convert(source));
		});

		return results;

	}

}
