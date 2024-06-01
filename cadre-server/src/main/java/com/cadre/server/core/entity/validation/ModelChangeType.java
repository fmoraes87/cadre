package com.cadre.server.core.entity.validation;

public enum ModelChangeType {
	//This is called before/after a model is created/updated into the database.
	TYPE_NEW,
	TYPE_CHANGE,
	AFTER_NEW,
	AFTER_CHANGE,
	//This is called before/after a model is removed/deleted from the database.
	TYPE_DELETE,
	TYPE_AFTER_DELETE;
	
}
