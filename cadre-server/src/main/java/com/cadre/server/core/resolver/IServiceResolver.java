package com.cadre.server.core.resolver;

import java.util.Optional;

public interface IServiceResolver {

	/**
	 * 
	 * @param type service interface
	 * @return holder for dynamic service
	 */
	<T> Optional<T> locate(Class<T> type);
	
	/**
	 * 
	 * @param type
	 * @param query
	 * @return
	 */
	<T> Optional<T> locate(Class<T> type, ResolverQuery query);
	
}
