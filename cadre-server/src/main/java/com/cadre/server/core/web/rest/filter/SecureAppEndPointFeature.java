package com.cadre.server.core.web.rest.filter;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import com.cadre.server.core.annotation.SecureAppEndPoint;

@Provider
public class SecureAppEndPointFeature implements DynamicFeature {

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
			SecureAppEndPoint annotation = resourceInfo.getResourceMethod().getAnnotation(SecureAppEndPoint.class);
			if (annotation==null) return;
			AppCredentialFilter filter = new AppCredentialFilter(annotation.databaseOperation());
			context.register(filter);
	}

}
