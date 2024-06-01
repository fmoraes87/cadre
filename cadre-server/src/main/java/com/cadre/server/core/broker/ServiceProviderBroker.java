package com.cadre.server.core.broker;

import javax.enterprise.inject.spi.CDI;

import com.cadre.server.core.cache.CCache;
import com.cadre.server.core.entity.MServiceProvider;
import com.cadre.server.core.service.ModelService;

public interface ServiceProviderBroker {

	public static CCache<String, MServiceProvider> customServiceProvider = new CCache<>("CustomServiceProvider", 5);
	public static final String INVALID_SERVICE_PROVIDER_CONFIGURATION = "@InvalidIdentityProviderConfiguration@";

	
	public static MServiceProvider getServiceProvider(String providerValue) {
		if (customServiceProvider.containsKey(providerValue)) {
			return customServiceProvider.get(providerValue);
		}else {
			ModelService modelService = CDI.current().select(ModelService.class).get();

			MServiceProvider identityProvider = modelService.getPO(null, MServiceProvider.TABLE_NAME ,MServiceProvider.COLUMNNAME_Value, providerValue);
			if (identityProvider!=null) {
				customServiceProvider.put(providerValue, identityProvider);
				return identityProvider;
			}else {
				return null;
			}
		}
		
	}

}
