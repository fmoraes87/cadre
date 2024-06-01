package com.cadre.server.core.converters;

public interface Populator<SOURCE,TARGET> {

	void populate (SOURCE source, TARGET target);
}
