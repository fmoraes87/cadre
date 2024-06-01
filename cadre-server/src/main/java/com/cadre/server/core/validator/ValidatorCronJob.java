package com.cadre.server.core.validator;

import javax.ws.rs.core.Response.Status;

import com.cadre.server.core.annotation.StaticValidator;
import com.cadre.server.core.cronjob.Scheduler;
import com.cadre.server.core.entity.MCronJob;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.entity.validation.ModelValidationException;

@StaticValidator(value = MCronJob.TABLE_NAME)
public class ValidatorCronJob extends AbstractModelValidator<MCronJob>{


	@Override
	protected void beforeDelete(String trxName, MCronJob cronJob) {
		if (cronJob.isActive()) {
			throw new ModelValidationException(Status.NOT_ACCEPTABLE.getStatusCode(),"@InvalidCronJobUpdate@");
		}	
	}

	@Override
	protected void beforeSave(String trxName, MCronJob cronJob)  throws ModelValidationException {
		if (!cronJob.isNew() && cronJob.isActive() && !cronJob.isValueChanged(POModel.COLUMNNAME_IsActive)) {
			throw new ModelValidationException(Status.NOT_MODIFIED.getStatusCode(),"@InvalidCronJobUpdate@");
		}else if(cronJob.isNew() || cronJob.isValueChanged(POModel.COLUMNNAME_IsActive)) {
			if (cronJob.isNew()  || cronJob.isActive()) {
				Scheduler.get().addJob(cronJob);						
			}
		}
	}


}
