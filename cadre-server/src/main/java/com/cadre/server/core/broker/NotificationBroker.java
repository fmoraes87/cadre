package com.cadre.server.core.broker;

import java.util.Map;

import javax.activation.DataSource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.MNotificationTemplate;
import com.cadre.server.core.entity.MServiceProvider;
import com.cadre.server.core.notification.NotificationProvider;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.resolver.ResolverQuery;
import com.cadre.server.core.resolver.ServiceType;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.FreeMarkerEngineUtils;


/**
 * 
 * @author fernando
 *
 */
@Singleton
public class NotificationBroker {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationBroker.class);

	public static final String NOTIFICATION_TYPE_EMAIL="email";
	
	@Inject
	private ModelService modelService;

	public NotificationBroker() {}

	/**
	 * Send Notification
	 * @param notificationType
	 * @param user
	 * @param template
	 * @param model
	 */
	public void sendNotificationlWithTemplate(String notificationType, String email, String templateName, Map<String, Object> model)
	{
			
		if (CadreEnv.isProduction()) {
			
			MNotificationTemplate template = modelService.getPO(null, MNotificationTemplate.TABLE_NAME, MNotificationTemplate.COLUMNNAME_Name,templateName);
			
			String body = FreeMarkerEngineUtils.mergeTemplate(template, model);

			ResolverQuery query = new ResolverQuery();
			query.put(MServiceProvider.COLUMNNAME_Value, notificationType);	
			query.put(MServiceProvider.COLUMNNAME_ServiceType, ServiceType.NOTIFICATION_PROVIDER);	
			NotificationProvider notificationProvider = DynamicServiceResolver.locate(NotificationProvider.class, query);
			notificationProvider.sendMessageTo(email, template.getHeader(), body,null,null);			
		}else {
			LOGGER.debug("Email Enviado");
		}

	}
	
	/**
	 * Send Notification
	 * @param notificationType
	 * @param user
	 * @param template
	 * @param model
	 */
	public void sendNotificationlWithoutTemplate(String notificationType, String emailTo, String header,  String message)
	{
		sendNotificationlWithAttachment(notificationType, emailTo, header,message,null,null);

	}
	
	/**
	 * Send Notification
	 * @param notificationType
	 * @param user
	 * @param template
	 * @param model
	 */
	public void sendNotificationlWithAttachment(String notificationType, String emailTo, String header,  String message, String [] filesName, DataSource [] sources)
	{
			
		if (CadreEnv.isProduction()) {
			
			ResolverQuery query = new ResolverQuery();
			query.put(MServiceProvider.COLUMNNAME_Value, notificationType);	
			query.put(MServiceProvider.COLUMNNAME_ServiceType, ServiceType.NOTIFICATION_PROVIDER);	
			NotificationProvider notificationProvider = DynamicServiceResolver.locate(NotificationProvider.class, query);
			notificationProvider.sendMessageTo(emailTo, header, message,filesName,sources);			
		}else {
			LOGGER.debug("Email Enviado");
		}

	}
	

}
