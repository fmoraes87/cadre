import { Criterion } from "./criterion.model";
import { SimpleExpression } from "./simple-expression.model";

export class Restrictions {

    static eq(propertyName: string , value: any ): Criterion {
        return new SimpleExpression(propertyName,value,OpType.EQUAL);
    }

    static ge(propertyName: string , value: any): Criterion {
        return new SimpleExpression(propertyName,value,OpType.GREAT_EQUAL);
    }

    static isNull(propertyName: string): Criterion {
        return new SimpleExpression(propertyName,null,OpType.EQUAL);
    }

    static like(propertyName: string , value: any): Criterion {
        return new SimpleExpression(propertyName,value,OpType.LIKE);
    }



    //between(String propertyName, Object lo, Object hi): Criterion
    //eqProperty(String propertyName, String otherPropertyName): PropertyExpression
    
    //geProperty(String propertyName, String otherPropertyName): PropertyExpression
    //gt(String propertyName, Object value) 
    //gtProperty(String propertyName, String otherPropertyName) 
    //in(String propertyName, Collection values) 
    //isNotNull(String propertyName) 
    //le(String propertyName, Object value)
    //leProperty(String propertyName, String otherPropertyName) 
    //lt(String propertyName, Object value) 
    //ltProperty(String propertyName, String otherPropertyName) 
    //ne(String propertyName, Object value) 
    //neProperty(String propertyName, String otherPropertyName) 
}

export enum OpType {
    EQUAL = "eq",
    GREAT_EQUAL = "ge",
    LIKE="contains"

}