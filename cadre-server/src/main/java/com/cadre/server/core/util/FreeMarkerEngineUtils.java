package com.cadre.server.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.entity.MNotificationTemplate;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

/**
 * 
 * @author fernando
 *
 */
public class FreeMarkerEngineUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FreeMarkerEngineUtils.class);

	private static final String FREEMARKER_VERSION = "2.3.30";
	
	private static Configuration freeMarkerConfiguration;
	static {
		freeMarkerConfiguration = new Configuration(new Version(FREEMARKER_VERSION));
		freeMarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		freeMarkerConfiguration.setDefaultEncoding(java.nio.charset.StandardCharsets.UTF_8.name());
	}
	
	public static String mergeTemplate (MNotificationTemplate template,Map<String,Object> model ) {
		if (template.isParseTemplate() && null != model && !model.isEmpty()) {
			try {
				Template t=  new Template(template.getName(), new StringReader(template.getTemplate()),freeMarkerConfiguration);
				Writer output = new StringWriter();
				t.process(model, output);
				
				return output.toString();
			} catch (IOException | TemplateException e) {
				LOGGER.error("mergeTemplate(template="+template.getName()+")", e);
				return template.getTemplate();
			}
			
		}else {
			return template.getTemplate(); 
		}
		
	}
}
