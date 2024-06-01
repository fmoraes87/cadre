package com.cadre.server.core.init;

import org.apache.commons.lang3.StringUtils;

public interface CadreBoostrap  {
	public void init();
	public String getExtensionName();
	public String getModelPackageName();
	public String getPopulatorsPackageName();
	
	default String getHandlersPackageName() {
		return StringUtils.EMPTY;
	}

}
