import { StringUtils } from '../util/string-utils';
import { Criterion } from './criterion.model';

export class Criteria {

    _count: boolean;
    _top: number;
    _skip: number;
    _resource: string;
    _criterions: Criterion[] = [];
    _orderByClause: string;
    _select: string [];

    select(select: string []){
      this._select = select;
      return this;
    }

    count(count: boolean) {
        this._count = count;
        return this;
    }

    top(top: number) {
        this._top = top;
        return this;
    }

    skip(skip: number) {
        this._skip = skip;
        return this;
    }

    from(resource: string) {
        this._resource = resource;
    }

    addCriterion(criterion: Criterion) {
        this._criterions.push(criterion);
    }

    orderBy(orderByClause: string) {
        this._orderByClause = orderByClause;
    }

    toQueryString(): string {
        let finalQuery = '?';

        if (this._criterions && this._criterions.length > 0) {
            finalQuery += '$filter=';
            this._criterions.forEach((criterion, index) => {
                if (index > 0) {
                    finalQuery += " and ";
                }
                finalQuery += criterion.toQueryString();
            });
        }

        if (this._count) {
            finalQuery += '&$count=true' ;
        }else{
            if (this._top) {
                finalQuery += '&$top='+this._top ;
            }

            if (this._skip) {
                finalQuery += '&$skip='+this._skip ;
            }
        }

        if (this._orderByClause) {
            finalQuery += '&$orderby=' + this._orderByClause;
        }

        if (this._select && this._select.length > 0) {
          finalQuery += '&$select='+ StringUtils.concat(this._select,',');
        }


        return finalQuery;
    }
}
