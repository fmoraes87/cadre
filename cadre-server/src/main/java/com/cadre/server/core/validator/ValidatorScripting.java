package com.cadre.server.core.validator;

import javax.ws.rs.core.Response.Status;

import org.codehaus.groovy.control.CompilationFailedException;

import com.cadre.server.core.annotation.StaticValidator;
import com.cadre.server.core.entity.MScripting;
import com.cadre.server.core.entity.validation.ModelValidationException;

import groovy.lang.GroovyShell;

@StaticValidator(value = MScripting.TABLE_NAME)
public class ValidatorScripting extends AbstractModelValidator<MScripting>{

	@Override
	protected void beforeSave(String trxName, MScripting scripting)  throws ModelValidationException {
		if (scripting.isGroovy() && (scripting.isNew() || scripting.isValueChanged(MScripting.COLUMNNAME_Content))) {
			GroovyShell sh = new GroovyShell();		
			try {
				sh.parse(scripting.getContent());
			} catch (CompilationFailedException ex) {
				throw new ModelValidationException(Status.BAD_REQUEST.getStatusCode(),ex.getMessage());
			}

		}
	}


}
