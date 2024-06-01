package com.cadre.server.core.entity.validation;

import com.cadre.server.core.entity.POModel;

public interface ModelValidator<T extends POModel> {

	default void modelChange(String trxName, T po, ModelChangeType changeType) {
		
	}

	default void beforeAccessData(String tableName) {
		
	}

	default void beforeAccessModel(T model) {
		
	}

}
