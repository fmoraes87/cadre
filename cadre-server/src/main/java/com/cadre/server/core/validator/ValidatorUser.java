package com.cadre.server.core.validator;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.core.Response.Status;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.annotation.DynamicValidator;
import com.cadre.server.core.broker.IdentityService;
import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.entity.MUserApp;
import com.cadre.server.core.entity.validation.ModelValidationException;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.PasswordUtils;

@DynamicValidator(value = MUser.TABLE_NAME)
public class ValidatorUser extends AbstractModelValidator<MUser> {

	private static final String ERROR_MSG_EMAIL_ALREADY_EXISTED = "@EmailAlreadyExists@";

	@Override
	protected void afterNew(String trxName, MUser user)  throws ModelValidationException {

		associateUserWithCurrentApp(user);			
		
		if (!user.isAccountVerified()) {
			
			IdentityService identityBroker = CDI.current().select(IdentityService.class).get();
			identityBroker.sendConfirmUserPassword(user);
						
		}
	}

	/**
	 * Associate user with current app
	 * @param trxName
	 * @param user
	 */
	private void associateUserWithCurrentApp(MUser user) {
		ModelService modelService =  CDI.current().select(ModelService.class).get();

		MUserApp userApp = modelService.createPO(user.get_TrxName(), MUserApp.TABLE_NAME);
		userApp.setAD_App_ID(CadreEnv.get_AD_App_ID());
		userApp.setAD_User_ID(user.getAD_User_ID());
		userApp.setIsActive(true);

		modelService.save(userApp);
		
		
	}

	@Override
	protected void beforeSave(String trxName, MUser user) {
		 boolean newRecord = user.isNew();
		 
		if (newRecord) {

			ModelService modelService = CDI.current().select(ModelService.class).get();

			SearchResult<MUser> userSearch = modelService.search(trxName, new JDBCQueryImpl.Builder(MUser.TABLE_NAME)
					.and(GenericCondition.equals(MUser.COLUMNNAME_EMailUser, user.getEMailUser().toLowerCase())).build());

			boolean throwException = false;
			MUser oldUser = userSearch.getSingleResult(throwException);
			if (oldUser == null) {
				String passwordWithOutHash = user.getUserPIN();
				user.setUserPIN(PasswordUtils.getHash(passwordWithOutHash));
			} else {
				throw new ModelValidationException(Status.BAD_REQUEST.getStatusCode(),ERROR_MSG_EMAIL_ALREADY_EXISTED);
			}
		}else {
			if (user.isValueChanged(MUser.COLUMNNAME_UserPIN)) {
				String passwordWithOutHash = user.getUserPIN();
				user.setUserPIN(PasswordUtils.getHash(passwordWithOutHash));
			}
		}
	}

}
