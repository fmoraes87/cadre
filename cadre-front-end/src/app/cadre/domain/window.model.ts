import { POModel } from "./po.model";
import { TabModel } from "./tab.model";

export class WindowModel extends POModel {
  static m_table_id: number = 7;
  static tableName: string = 'AD_Window';

  _tabs: TabModel []= [];
  _id: number= Math.random();

  constructor() {
    super();
    this.initColumnsName(
      [
      "AD_Window_ID",
      "Name",
      "Description"
    ]);
  }

  get poTableName(){
      return WindowModel.tableName;
  }

  get table_ID() {
      return WindowModel.m_table_id;
  }

  get name(){
    return super.getValueByColumnName("Name");
  }

  get tabs(): TabModel  [] {
    return this._tabs;
  }



}
