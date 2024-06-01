import { POModel } from "./po.model";

export class RefListModel extends POModel {
  static readonly m_table_id: number = 100;
  static readonly tableName: string = 'AD_Ref_List';

  constructor() {
    super();
    this.initColumnsName(
      [
      "name",
      "value"
    ]);
  }

  get poTableName(){
      return RefListModel.tableName;
  }

  get table_ID() {
      return RefListModel.m_table_id;
  }
  get name(): any {
    return this.getValueByColumnName("name");
  }

  get value(): any {
    return this.getValueByColumnName("value");
  }

  
}
