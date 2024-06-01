import { POModel } from "./po.model";
import { RefListModel } from "./ref_list.model";


export class ReferenceModel extends POModel {
  static readonly m_table_id: number = 102;
  static readonly tableName: string = 'AD_Reference';

  _id: number= Math.random();
  _refListElements: RefListModel [];

  constructor() {
    super();
    this.initColumnsName(
      [
      "refListElements"
    ]);
  }

  get poTableName(){
      return ReferenceModel.tableName;
  }

  get table_ID() {
      return ReferenceModel.m_table_id;
  }

}
