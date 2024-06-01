import { Criterion } from "./criterion.model";

export class CustomExpression extends Criterion {

    private _expression: string;

    constructor(expression: string ) {
        super();
        this._expression = expression;
    }

    toQueryString():string{
        return this._expression;
    }

}