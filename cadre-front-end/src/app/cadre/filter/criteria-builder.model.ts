import { Criteria } from "./criteria.model";
import { Criterion } from "./criterion.model";

export class CriteriaBuilder {

  private criteria: Criteria = new Criteria();

  select(select: string[]) {
    this.criteria.select(select);
    return this;
  }

  count(count: boolean) {
      this.criteria.count(count);
      return this;
  }

  top(top: number) {
      this.criteria.top(top);
      return this;
  }

  skip(skip: number) {
      this.criteria.skip(skip);
      return this;
  }

  from(resource: string) {
      this.criteria.from(resource);
      return this;
  }

  addCriterion(criterion: Criterion): CriteriaBuilder {
      this.criteria.addCriterion(criterion);
      return this;
  }


  orderBy(orderByClause: string): CriteriaBuilder {
      this.criteria.orderBy(orderByClause);
      return this;
  }

  build(): Criteria {
      return this.criteria;
  }
}
