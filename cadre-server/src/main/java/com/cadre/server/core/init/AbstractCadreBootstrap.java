package com.cadre.server.core.init;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Stream;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.data.Entity;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.CadreModel;
import com.cadre.server.core.annotation.CustomBuilder;
import com.cadre.server.core.annotation.CustomPopulator;
import com.cadre.server.core.annotation.StaticHandler;
import com.cadre.server.core.boundary.ServiceProvider;
import com.cadre.server.core.builders.CustomCadreDTOBuilder;
import com.cadre.server.core.builders.DefaultDTOBuilderProvider;
import com.cadre.server.core.converters.Populator;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.entity.validation.ValidationEngine;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.persistence.exception.DBException;
import com.cadre.server.core.persistence.jdbc.StatementFactory;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.populators.DefaultPopulatorProvider;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.resolver.ModelResolver;
import com.cadre.server.core.resolver.ServiceType;


public abstract class AbstractCadreBootstrap implements CadreBoostrap {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCadreBootstrap.class);

	
	private String trxName;
	
	public AbstractCadreBootstrap() {

	}

	//Run this before web application is started
	public final void init() {
		Trx trx = Trx.get(Trx.createTrxName(), true);
		trxName = trx.getTrxName();
			try {
				loadModels();
				loadCustomServiceProviders();
				loadDynamicValidators();
				
				loadProcess();
				loadPopulators();
				
				loadHandlers();
				
				initCustomResources();

			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOGGER.error("init()", e);
				if (trx != null) {
					trx.rollback();
					trx.close();
					trx = null;
				}
				System.exit(1);
			}finally {
				if (trx != null) {
					trx.commit();
					trx.close();
				}
			}
			

		//Engines
		
	}



	protected void initCustomResources() {};

	private void loadProcess() {
		//SvrProcessEngine.get(); //Initialize Process
	}
	

	private void loadPopulators() {
		String packages = getPopulatorsPackageName();
		if (StringUtils.isNotBlank(packages)) {
			Stream.of(packages.split(";")).forEach( packageName -> {
				processPopulators(packageName);
				processDTOBuilders(packageName);
			});
		}

	}

	private void processDTOBuilders(String packageName) {
		Reflections reflections = new Reflections(packageName);
		Set<Class<? extends CustomCadreDTOBuilder>> services = reflections.getSubTypesOf(CustomCadreDTOBuilder.class);
		services.stream().forEach(clazz -> {
				CustomBuilder annotation = clazz.getAnnotation(CustomBuilder.class);
				if (annotation!=null) {
					String resource = annotation.value();
					DefaultDTOBuilderProvider.get().add(resource, clazz);
				}
		});		
	}

	@SuppressWarnings("unchecked")
	private void processPopulators(String packageName) {
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> services = reflections.getTypesAnnotatedWith(CustomPopulator.class);
		services.stream().forEach(clazz -> {
			try {
				CustomPopulator annotation = clazz.getAnnotation(CustomPopulator.class);
				String key = annotation.value();
				DefaultPopulatorProvider.get().load(key,(Populator<POModel, Entity>) clazz.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.error("processPopulators("+packageName +")", e);

				System.exit(1);
			}
		});
	}

	private void loadDynamicValidators() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT mv.ModelValidationClass ");
		sql.append(" FROM AD_ModelValidator mv ");
		sql.append(" 		INNER JOIN AD_Extension ex on ex.ad_extension_id=mv.ad_extension_id ");	
		sql.append(" WHERE mv.isActive='Y' and mv.ModelValidationClass is not null and ex.isActive='Y' ");
		sql.append(" 	AND ex.value= ? ");
		sql.append(" ORDER BY mv.SeqNo ");

		try (final PreparedStatement pstmt = StatementFactory.newCPreparedStatement(trxName, sql.toString())) {
			pstmt.setString(1, getExtensionName());

			try (ResultSet rs = pstmt.executeQuery()) {
				ValidationEngine engine = CDI.current().select(ValidationEngine.class).get();
				while (rs.next()) {
					engine.loadValidatorClass(rs.getString(1));
				}
			}

		} catch (SQLException e) {
			LOGGER.error("loadDynamicValidators()", e);
			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void loadModels() {
		String packageName = getModelPackageName();
		if (StringUtils.isNotBlank(packageName)) {
			Reflections reflections = new Reflections(packageName);
			Set<Class<?>> services = reflections.getTypesAnnotatedWith(CadreModel.class);
			services.stream().forEach(clazz -> {
				CadreModel annotation = clazz.getAnnotation(CadreModel.class);
				String key = annotation.value();
				ModelResolver.get().load(key, (Class<? extends POModel>) clazz);
			});		
			
		}		
	}


	private void loadHandlers() {
		String packageName = getHandlersPackageName();
		if (StringUtils.isNotBlank(packageName)) {
			Reflections reflections = new Reflections(packageName);
			Set<Class<?>> services = reflections.getTypesAnnotatedWith(StaticHandler.class);
			services.stream().forEach(clazz -> {
				try {
					ServiceProvider service = (ServiceProvider) clazz.newInstance();
					service.initialize();
				} catch (InstantiationException | IllegalAccessException e) {
					LOGGER.error("initService("+ clazz+")" , e);

					throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@ServiceNotSupported@");
				}
			});		
		}
					
	}

	private void loadCustomServiceProviders() {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT sp.value, sp.servicetype, sp.classname ");
		sql.append(" FROM AD_ServiceProvider sp ");
		sql.append(" 		INNER JOIN AD_Extension ex on ex.ad_extension_id=sp.ad_extension_id ");
		sql.append(" WHERE ex.isActive='Y' and sp.isActive='Y' ");
		sql.append(" 	AND ex.value= ? ");

		try (final PreparedStatement pstmt = StatementFactory.newCPreparedStatement(trxName, sql.toString())) {
			pstmt.setString(1, getExtensionName());
			
			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					final String value= rs.getString("value");
					final String type= rs.getString("servicetype");
					final String classname= rs.getString("classname");

					DynamicServiceResolver.register(value,ServiceType.getAccessLevelByCode(type),classname);
				}
			}

		} catch (SQLException e) {
			LOGGER.error("loadCustomServiceProviders()", e);
			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
		}

	}

	public String getTrxName() {
		return trxName;
	}
	
	

}
