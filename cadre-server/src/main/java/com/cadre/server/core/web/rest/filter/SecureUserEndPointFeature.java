package com.cadre.server.core.web.rest.filter;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import com.cadre.server.core.annotation.SecureUserEndPoint;

@Provider
public class SecureUserEndPointFeature implements DynamicFeature {

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
			SecureUserEndPoint annotation = resourceInfo.getResourceMethod().getAnnotation(SecureUserEndPoint.class);
			if (annotation==null) return;
			BearerTokenFilter filter = new BearerTokenFilter(annotation.databaseOperation());
			context.register(filter);
	}

}
