package com.cadre.server.core.process;

import javax.enterprise.util.AnnotationLiteral;

import com.cadre.server.core.annotation.SvrProcess;

public class ProcessNameLiteral extends AnnotationLiteral<SvrProcess> implements SvrProcess {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private final String processValue;

    public ProcessNameLiteral(String processValue) {
        this.processValue = processValue;
        
    }

	@Override
	public String value() {
		return processValue;
	}
}