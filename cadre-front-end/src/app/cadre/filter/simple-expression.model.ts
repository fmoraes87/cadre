import { Criterion } from './criterion.model';
import { OpType } from './restrictions.factory';

export class SimpleExpression extends Criterion {

    private _propertyName: string;
    private _value: any;
    private _op: OpType;

    constructor(propertyName: string , value: any , op: OpType ) {
        super();
        this._propertyName = propertyName;
        this._value = value;
        this._op= op;
    }

    toQueryString():string{
        if (this._op== OpType.EQUAL || this._op== OpType.GREAT_EQUAL ){
            if (typeof this._value==='string') {
                return this._propertyName +  ' ' + this._op + ' \'' + this._value + '\'';
            }else{
                return this._propertyName +   ' ' + this._op + ' ' + this._value;
    
            }
        }else if (this._op == OpType.LIKE){
            return  this._op + '(' + this._propertyName +  ', ' + ' \'' + this._value + '\')';
        }

        return null;
    }

   
}