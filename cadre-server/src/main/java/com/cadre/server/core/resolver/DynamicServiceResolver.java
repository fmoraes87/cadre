package com.cadre.server.core.resolver;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.CustomService;
import com.cadre.server.core.boundary.ServiceProvider;
import com.cadre.server.core.entity.MServiceProvider;
import com.cadre.server.core.entity.ServiceLoadMode;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.util.PropertiesUtils;

/**
 * A service locator looks up services.
 * This is the central authority for cadre service definition,
 * because each service defined has to be looked up via this interface.
 * 
 * A service is an implementation for an interface, expose through extension
 *
 * @author fernando
 *
 */
public class DynamicServiceResolver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicServiceResolver.class);

	@SuppressWarnings("rawtypes")
	private static Map<Class<?>,List>m_serviceResolverFactoryList = new HashMap<>();

	
	public static <T extends ServiceProvider> T locate(Class<T> type) {
		return locate(type, null);
	}

	public static<T extends ServiceProvider> T locate(Class<T> type, ResolverQuery query) {
		@SuppressWarnings("unchecked")
		List<ServiceHolder<T>> serviceHolderList = m_serviceResolverFactoryList.get(type);
		if (CollectionUtils.isNotEmpty(serviceHolderList)) {
			if(query==null) {
				return serviceHolderList.get(serviceHolderList.size()-1).getService();				
			}else {
				Optional<ServiceHolder<T>> serviceHolder = serviceHolderList.stream().filter(s -> 
					s.getProperties().entrySet().stream()
				      .allMatch(e -> e.getValue().equals(query.get(e.getKey())))
				).findFirst();

				if (serviceHolder.isPresent()) {
					return serviceHolder.get().getService();
				}else{
					throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@ServiceNotSupported@");
				}
			}
		}else {
			throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@ServiceNotImplemented@");
		}
	}

	public static void register(String value, ServiceType type, String classname) {
		try {
			Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(classname);

			Annotation[] annotations = clazz.getAnnotations();
			boolean hasAnnotation = false;
			for (Annotation annotation : annotations) {
				if (annotation instanceof CustomService) {
					CustomService myAnnotation = (CustomService) annotation;
					ServiceLoadMode loadMode = myAnnotation.loadMode();
					boolean register = myAnnotation.register();

					Map<String,Object> properties = new HashMap<>();
					properties.put(MServiceProvider.COLUMNNAME_Value,value);
					properties.put(MServiceProvider.COLUMNNAME_ServiceType,type);
					
					if (StringUtils.isNotEmpty(myAnnotation.properties())) {
						properties.putAll(PropertiesUtils.parseMap(myAnnotation.properties()));
					}

					ServiceHolder serviceHolder = new ServiceHolder(properties, clazz, loadMode);

					if (register) {
						if (m_serviceResolverFactoryList.containsKey(myAnnotation.serviceId())) {
							m_serviceResolverFactoryList.get(myAnnotation.serviceId()).add(serviceHolder);
						} else {
							List<ServiceHolder> services = new ArrayList<>();
							services.add(serviceHolder);
							m_serviceResolverFactoryList.put(myAnnotation.serviceId(), services);

						}
					}
					hasAnnotation = true;
					break;
				}
			}

			if (!hasAnnotation) {
				throw new IllegalArgumentException("Invalid Class: " + classname);
			}

		} catch (ClassNotFoundException ex) {
			LOGGER.error("register(" + classname + ")", ex);

			System.exit(1);
		}
	}
    

}

class ServiceHolder<T extends ServiceProvider>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceHolder.class);

	private T service;
	private Class<?> clazz;
	private Map<String, Object> properties;
	
	ServiceHolder(Map<String,Object> properties, Class<?> clazz, ServiceLoadMode loadMode) {
		this.clazz=clazz;
		this.properties=properties;
		if (loadMode==ServiceLoadMode.EAGER) {
			initService();
		}
	}
	

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void initService() {
		try {
			service = (T) clazz.newInstance();
			service.initialize();
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.error("initService("+ clazz+")" , e);

			throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@ServiceNotSupported@");
		}
	}
	
	//Implements a Singleton
	public T getService() {
		if (service==null) {
			initService();
		}
		return service;
	}
}
