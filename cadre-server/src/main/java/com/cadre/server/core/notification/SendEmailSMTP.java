package com.cadre.server.core.notification;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.enterprise.inject.spi.CDI;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.annotation.CustomService;
import com.cadre.server.core.broker.ServiceProviderBroker;
import com.cadre.server.core.entity.MClient;
import com.cadre.server.core.entity.MMailConfig;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.service.ModelService;

@CustomService(
		serviceId = NotificationProvider.class
)
public class SendEmailSMTP implements NotificationProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailSMTP.class);

	private static final String MAIL_SMTP_SSL_TRUST = "mail.smtp.ssl.trust";

	protected static final String EMAIL_SMTP = "email";

	private static final String MAIL_SMTP_PORT = "mail.smtp.port";
	private static final String MAIL_SMTP_HOST = "mail.smtp.host";
	private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

	public void sendMessageTo(String email, String strSubject, String strMessage,  String[] filesName,  DataSource [] wrapFilesContent) {
		
		if (StringUtils.isBlank(email)) {
			return;
		}

		ModelService modelService =  CDI.current().select(ModelService.class).get();

		MClient client =  modelService.getPO(null, MClient.TABLE_NAME,CadreEnv.getAD_Client_ID());
		if (client.getAD_MailConfig_ID()==0) {
			throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),ServiceProviderBroker.INVALID_SERVICE_PROVIDER_CONFIGURATION);
		}
		
		MMailConfig mailConfig = modelService.getPO(null, MMailConfig.TABLE_NAME, client.getAD_MailConfig_ID());
				
		Properties prop = new Properties();
		prop.put(MAIL_SMTP_AUTH, true);
		prop.put(MAIL_SMTP_STARTTLS_ENABLE, Boolean.TRUE);
		prop.put(MAIL_SMTP_HOST, mailConfig.getSMTPHost());
		prop.put(MAIL_SMTP_PORT, mailConfig.getSMTPPort());
		prop.put(MAIL_SMTP_SSL_TRUST, mailConfig.getSMTPHost());
		
		Authenticator auth =  new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(mailConfig.getRequestUser(), mailConfig.getRequestUserPW()	);
		    }
		};
		
		Session session = Session.getInstance(prop,auth);		
		CompletableFuture.runAsync(() -> {
	        try {
	    		
	        	Message msg = new MimeMessage(session);
	            msg.setFrom(new InternetAddress(mailConfig.getRequestEMail()));
	            msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email, false));
	            msg.setSubject(strSubject);
				msg.setSentDate(new Date(System.currentTimeMillis()));
				
		        
				if (null!=filesName && filesName.length > 0) {
					MimeBodyPart messageBodyPart = new MimeBodyPart();
					messageBodyPart.setContent(strMessage,"text/html; charset="+java.nio.charset.StandardCharsets.UTF_8.name());
					
					 Multipart multipart = new MimeMultipart();
					 multipart.addBodyPart(messageBodyPart);
					 
					for (int i =0; i < filesName.length ; i++) {
						String fileName= filesName[i];
						DataSource source = wrapFilesContent[i];
						
						MimeBodyPart attachPart = new MimeBodyPart();

						attachPart.setDataHandler(new DataHandler(source)); 
						attachPart.setFileName(fileName);  
						multipart.addBodyPart(attachPart);
					}
					 
					 msg.setContent(multipart);
				}else {
					msg.setContent(strMessage,"text/html; charset="+java.nio.charset.StandardCharsets.UTF_8.name());
				}
				
				Thread.currentThread().setContextClassLoader( getClass().getClassLoader());
				
				Transport.send(msg);
				
	
	        }catch (MessagingException  me) {
				Exception ex = me;
				StringBuffer sb = new StringBuffer("(ME)");
				boolean printed = false;
	
				do
				{
					if (ex instanceof SendFailedException)
					{
						SendFailedException sfex = (SendFailedException)ex;
						Address[] invalid = sfex.getInvalidAddresses();
						if (!printed)
						{
							if (invalid != null && invalid.length > 0)
							{
								sb.append (" - Invalid:");
								for (int i = 0; i < invalid.length; i++)
									sb.append (" ").append (invalid[i]);
	
							}
							Address[] validUnsent = sfex.getValidUnsentAddresses ();
							if (validUnsent != null && validUnsent.length > 0)
							{
								sb.append (" - ValidUnsent:");
								for (int i = 0; i < validUnsent.length; i++)
									sb.append (" ").append (validUnsent[i]);
							}
							Address[] validSent = sfex.getValidSentAddresses ();
							if (validSent != null && validSent.length > 0)
							{
								sb.append (" - ValidSent:");
								for (int i = 0; i < validSent.length; i++)
									sb.append (" ").append (validSent[i]);
							}
							printed = true;
						}
						if (sfex.getNextException() == null) {
							sb.append(" ").append(sfex.getLocalizedMessage());
						}
					}
					else if (ex instanceof AuthenticationFailedException)
					{
						sb.append(" - Invalid Username/Password - " + auth);
					}
					else	//	other MessagingException 
					{
						String msg = ex.getLocalizedMessage();
						if (msg == null)
							sb.append(": ").append(ex.toString());
						else
						{
							if (msg.indexOf("Could not connect to SMTP host:") != -1)
							{
								int index = msg.indexOf('\n');
								if (index != -1)
									msg = msg.substring(0, index);
								String cc = Integer.toString(CadreEnv.getAD_Client_ID());
								msg += " - AD_Client_ID=" + cc;
							}
							String className = ex.getClass().getName();
							if (className.indexOf("MessagingException") != -1)
								sb.append(": ").append(msg);
							else
								sb.append(" ").append(className).append(": ").append(msg);
						}
					}
					//	Next Exception
					if (ex instanceof MessagingException)
						ex = ((MessagingException)ex).getNextException();
					else
						ex = null;
				}	
				while (ex != null);	//	error loop       
				
				LOGGER.error(sb.toString(), me);
	
				throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@ErrorSendEmail@",sb.toString());
	        }
	
		});
	}

}
