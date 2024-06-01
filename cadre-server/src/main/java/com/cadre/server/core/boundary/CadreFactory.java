package com.cadre.server.core.boundary;

import java.io.Serializable;
import java.util.List;

public interface CadreFactory<K extends Serializable,T> {

	void load(K key, T object);
	
	default List<T> getElements(K key){
		throw new UnsupportedOperationException();
	}
	
	default T getElement(K key) {
		throw new UnsupportedOperationException();
	}

}