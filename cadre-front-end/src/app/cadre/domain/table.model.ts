import { ColumnModel } from "./column.model";
import { POModel } from "./po.model";

export class TableModel extends POModel{
    static readonly m_table_id: number = 4;
    static readonly tableName: string = 'AD_Table';

    	/** Column name UpdatedBy */
  static readonly COLUMNNAME_TableName: string = "TableName";
  static readonly COLUMNNAME_AD_Table_ID: string = "AD_Table_ID";

    private _columns: ColumnModel[] = [] ;

    constructor() {
        super();
        this.initColumnsName(
            [
                "AD_Table_ID",
                "IsSecurityEnabled",
                "Help",
                "IsHighVolume",
                "IsView",
                "Name",
                "Description",
                TableModel.COLUMNNAME_TableName,
                "AD_Client_ID",
                "AD_Org_ID",
                "IsActive",
                "Created",
                "CreatedBy",
                "Updated",
                "UpdatedBy",
                "IsDeleteable",
                "IsChangeLog",
                "EntityType"
          ]);
    }

    get poTableName(){
        return TableModel.tableName;
    }

    get table_ID() {
        return TableModel.m_table_id;
    }

    get ad_table_id(){
      return super.getValueByColumnName("AD_Table_ID");
    }

    set ad_table_id(p_ad_tabl_id){
      super.setValueByColumnName("AD_Table_ID",p_ad_tabl_id);
    }

    get tableName(){
        return super.getValueByColumnName(TableModel.COLUMNNAME_TableName);
    }

    set tableName(tableName){
      super.setValueByColumnName(TableModel.COLUMNNAME_TableName,tableName);
    }

    get columns(){
        return this._columns;
    }

    getIdentifiers(){
      let identifiers: string[] = [];
      this._columns.forEach(c => {
        if (c.is_identifier){
          identifiers.push(c.columnName);
        }
      });

      return identifiers;
    }

}
