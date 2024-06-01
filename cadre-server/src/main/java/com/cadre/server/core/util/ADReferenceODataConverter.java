package com.cadre.server.core.util;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

import com.cadre.server.core.entity.MColumn;

public class ADReferenceODataConverter {

	public static EdmPrimitiveTypeKind get(int displayType){
		 if (DisplayType.isID(displayType) || displayType == DisplayType.Integer){
			return EdmPrimitiveTypeKind.Int32;
		}else if (DisplayType.isNumeric(displayType)){
			return EdmPrimitiveTypeKind.Decimal;
		}else if (displayType == DisplayType.YesNo){
			return EdmPrimitiveTypeKind.Boolean;
		}else if (displayType== DisplayType.DateTime ||displayType== DisplayType.Date){
			return EdmPrimitiveTypeKind.DateTimeOffset;
		}else if (displayType== DisplayType.Time){
			return EdmPrimitiveTypeKind.TimeOfDay;
		}else{
			return EdmPrimitiveTypeKind.String;
		}
	}
}

