import { Injectable } from "@angular/core";
import { NgxIndexedDBService } from "ngx-indexed-db";
import { ColumnModel } from "../domain/column.model";
import { TableModel } from "../domain/table.model";
import { CriteriaBuilder } from "../filter/criteria-builder.model";
import { Restrictions } from "../filter/restrictions.factory";
import { POService } from "./po.service";

@Injectable({
  providedIn: "root",
})
export class TableService {
  constructor(
    private poService: POService,
    private dbService: NgxIndexedDBService
  ) {}

  getTableByIdFromDatabase(tableId: number): Promise<TableModel> {
    return new Promise<TableModel>((resolve) => {
      let tableModel: TableModel = null;

      this.poService
        .getPO(TableModel.tableName, tableId, true)
        .toPromise()
        .then((data: any) => {
          //Load table Info
          tableModel = new TableModel();
          tableModel.loadFromJSON(data);

          //Get table Columns
          let filter = new CriteriaBuilder()
            .from(ColumnModel.tableName)
            .addCriterion(Restrictions.eq("AD_Table_ID", tableId))
            .build();

          this.poService.getTableData(filter, true).subscribe((resp: any) => {
            let data = resp.value;
            if (data) {
              let keys: any[] = Object.keys(data);

              keys.forEach((currentKey) => {
                let columnJSON = data[currentKey];
                let columnModel = new ColumnModel();
                columnModel.loadFromJSON(columnJSON);
                tableModel.columns.push(columnModel);
              });


              this.dbService.add(TableModel.tableName, {
                TableName: tableModel.tableName,
                AD_Table_ID: tableId,
              }).subscribe(r => {
                tableModel.columns.forEach(columnModel =>{
                  this.dbService.add(ColumnModel.tableName, {
                    ColumnName: columnModel.columnName,
                    AD_Reference_ID: columnModel.ad_reference_id,
                    AD_Column_ID: columnModel.ad_column_id,
                    AD_Table_ID: columnModel.ad_table_id
                  }).subscribe((key) => {
                    console.log('key: ', key);
                  });

                })
              })

              resolve(tableModel);
            }
          });
        });
    });
  }

  getTableByIdFromCache(tableId: number): Promise<TableModel> {
    return new Promise<TableModel>((resolve) => {
      let tableModel: TableModel = null;
      //Get Table Info
      this.dbService
        .getByKey(TableModel.tableName, tableId)
        .subscribe((tableData) => {
          tableModel = new TableModel();

          if (tableData) {
            tableModel.tableName = tableData['TableName'];
            tableModel.ad_table_id = tableData['AD_Table_ID'];

            console.log(tableModel.ad_table_id + " loading from cache");

            this.dbService
              .getAllByIndex(
                ColumnModel.tableName,
                "AD_Table_ID",
                IDBKeyRange.only(tableModel.ad_table_id)
              )
              .subscribe((columnsData) => {
                if (columnsData && columnsData.length > 0) {
                  let keys: any[] = Object.keys(columnsData);

                  keys.forEach((currentKey) => {
                    let columnJSON = columnsData[currentKey];
                    let columnModel = new ColumnModel();
                    columnModel.loadFromJSON(columnJSON);
                    tableModel.columns.push(columnModel);
                  });

                }

                resolve(tableModel);
              });
          }else{
            resolve(undefined);

          }
        });
    });
  }


  async getTableById(tableId: number) {
    let tableModel = await this.getTableByIdFromCache(tableId);

     if (!tableModel){
      tableModel = await this.getTableByIdFromDatabase(tableId);
     }

     return new Promise<TableModel>((resolve) => {resolve(tableModel);});
  }
}
