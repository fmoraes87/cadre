import { ColumnModel } from "./column.model";
import { POModel } from "./po.model";
import { TableModel } from "./table.model";


export class GenericPO extends POModel {
    private _tableID: number;	
    private _tableName: string;
    
    constructor(table: TableModel){
        super();
        this._tableID = table.table_ID;
        this._tableName = table.tableName;

        this.initColumns(table.columns);
        
    }

    initColumns(columns: ColumnModel[]){
        if (columns){
            let columnsName: string[] = [];
            columns.forEach(column => {
                if (column){
                    columnsName.push(column.columnName)
                }
            });

            this.initColumnsName(columnsName);
        }
    }

    get poTableName(){
        return this._tableName;
    }

    get table_ID() {
        return this._tableID;
    }
}