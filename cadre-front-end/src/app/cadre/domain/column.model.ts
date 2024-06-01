import { POModel } from "./po.model";

export class ColumnModel extends POModel{
    static readonly m_table_id: number = 5;
    static readonly tableName: string = 'AD_Column';

    constructor() {
        super();
        this.initColumnsName(
            [
                "AD_Column_ID",
                "IsKey",
                "Name",
                "Description",
                "Help",
                "AD_Table_ID",
                "ColumnName",
                "AD_Reference_ID",
                "AD_Client_ID",
                "AD_Org_ID",
                "IsActive",
                "Created",
                "CreatedBy",
                "Updated",
                "UpdatedBy",
                "EntityType",
                "IsIdentifier"
          ]);
    }


    get poTableName(){
        return ColumnModel.tableName;
    }

    get table_ID() {
        return ColumnModel.m_table_id;
    }

    get columnName(){
        return super.getValueByColumnName('ColumnName');
    }

    get ad_reference_id(){
        return super.getValueByColumnName('AD_Reference_ID');
    }

    get ad_column_id () {
      return super.getValueByColumnName('AD_Column_ID');

    }

    get ad_table_id () {
      return super.getValueByColumnName('AD_Table_ID');

    }

    get is_identifier () {
      return super.getValueByColumnName('IsIdentifier');

    }



}
