package com.cadre.server.core.service.impl;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.annotation.CustomService;
import com.cadre.server.core.dto.RequestDataAccess;
import com.cadre.server.core.entity.DatabaseOperation;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.jdbc.RDBMS;
import com.cadre.server.core.service.DataAccessService;
import com.cadre.server.core.util.SecurityUtils;

@CustomService(serviceId = DataAccessService.class)
public class DataAccessServiceImpl implements DataAccessService {

	 private static final String SQL_USER_HAS_ACCESS = "SELECT COALESCE(sum(x),0) "
			 			+ " FROM ( SELECT 1 as x"
						+ " 	FROM AD_User_Roles ur"
						+ "  INNER JOIN AD_Role r on r.ad_role_id= ur.ad_role_id and r.IsActive='Y' "
						+ "  INNER JOIN AD_Object_Access oa on oa.AD_Role_ID = r.AD_Role_ID "
						+ "					and oa.IsActive='Y' "
						+  " 				and OA.AD_Resource_Type_ID = ? "
						+ "					and case "
						+ "							when isExactlyMatch='Y' and lower(oa.value)=lower(?) then 1 "
						+ "							when isExactlyMatch='N' and lower(?) like  lower(oa.value)   then 1"
						+ "							else 0 "
						+ "					end = 1 "

						+ " WHERE ur.AD_User_ID=? "
						+ "			and ur.IsActive='Y' "
						+ "			and ur.AD_Client_ID in(0,?) "
						+ "			and ur.AD_Org_ID in(0,?)"
						+ "			and oa.isReadOnly in ('N', ?) "
						//User Admin
						+ " UNION "
						+ " SELECT 1 as x"
						+ " FROM AD_User  u"
						+ " WHERE u.AD_User_ID=? and u.isAdmin='Y' and u.IsActive='Y' "
						// public resource
						+ " UNION "
						+ " SELECT 1 as x"
						+ " FROM AD_Table  t"
						+ " WHERE ? = 1" //AD_Resource_Type_ID 
					    + "			and ? = 'Y' " //valid just readonly operation		
						+ "			and lower(t.tablename)=lower(?) " //value
						+ "			and t.IsPublic='Y' and t.isActive='Y' ) as T ";
					
	 
	private static final String SQL_APP_CLIENT_HAS_ACCESS = "SELECT COALESCE(sum(x),0) "
						+ " FROM ( SELECT 1 as x"
						+ " FROM AD_OAuth_Client_Roles cl"
						+ "  INNER JOIN AD_Role r on r.ad_role_id= cl.ad_role_id and r.IsActive='Y' "
						+ "  INNER JOIN AD_Object_Access oa on oa.AD_Role_ID = r.AD_Role_ID "
						+ "					and oa.IsActive='Y' "
						+  " 				and OA.AD_Resource_Type_ID = ? "
						+ "					and case "
						+ "							when isExactlyMatch='Y' and lower(oa.value)=lower(?) then 1 "
						+ "							when isExactlyMatch='N' and lower(?) like  lower(oa.value)   then 1"
						+ "							else 0 "
						+ "					end = 1 "
						+ " WHERE cl.AD_OAuth2_Client_ID=? "
						+ "			and cl.IsActive='Y' "
						+ "			and cl.AD_Client_ID in(0,?) "
						+ "			and cl.AD_Org_ID in(0,?)"
						+ "			AND oa.isReadOnly in ('N', ?) "
						+ " UNION "
						+ " SELECT 1 as x"
						+ " FROM AD_OAuth2_Client  oc"
						+ " WHERE oc.AD_OAuth2_Client_ID=? and oc.isAdmin='Y' ) as T ";
	

	// Inner class to provide instance of class 
	/*private static class Singleton {
		private static final DataAccessService INSTANCE = new DataAccessServiceImpl();
	}

	public static DataAccessService get() {
		return Singleton.INSTANCE;
	}*/

	@Override
	public boolean isClientAllowed(String trxName, RequestDataAccess request) {
			String grantType = CadreEnv.getGrantType();
			if (grantType.equals(SecurityUtils.GRANT_TYPE_CLIENT_CREDENTIALS)) {
				return isAppAllowed(trxName,request);
			}else {
				return isUserAllowed(trxName,request);							
			}
	}
	
	private boolean isAppAllowed(String trxName,RequestDataAccess request) {
		boolean readOnly = request.getOp()==DatabaseOperation.READ ? true:false;

		int count = RDBMS.getSQLValueEx(trxName, SQL_APP_CLIENT_HAS_ACCESS, request.getType().getCode()
				,request.getResource().toLowerCase()
				,request.getResource().toLowerCase()
				,CadreEnv.getContextAsInt(CadreEnv.AD_OAuth2_Client_ID)
				,CadreEnv.getAD_Client_ID()
				,CadreEnv.getAD_Org_ID()
				,(readOnly ? POModel.YES_VALUE: POModel.NO_VALUE)
				,CadreEnv.getContextAsInt(CadreEnv.AD_OAuth2_Client_ID));
		return count > 0;
	}
	
	public boolean isUserAllowed(String trxName,RequestDataAccess request) {
		boolean readOnly = request.getOp()==DatabaseOperation.READ ? true:false;
			
		int count = RDBMS.getSQLValueEx(trxName, SQL_USER_HAS_ACCESS, 
				request.getType().getCode()
				,request.getResource().toLowerCase()
				,request.getResource().toLowerCase()
				,CadreEnv.getAD_User_ID()
				,CadreEnv.getAD_Client_ID()
				,CadreEnv.getAD_Org_ID()
				,(readOnly ? POModel.YES_VALUE: POModel.NO_VALUE)	
				//User
				,CadreEnv.getAD_User_ID()
				//Public Resource
				, request.getType().getCode()
				,(readOnly ? POModel.YES_VALUE: POModel.NO_VALUE)
				,request.getResource().toLowerCase()
			);
		
		return count > 0;
	}
	
	
}
