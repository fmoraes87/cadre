import { Injectable } from "@angular/core";
import { POService } from "./po.service";

import { NgxIndexedDBService } from "ngx-indexed-db";
import { WindowModel } from "../domain/window.model";
import { TabModel } from "../domain/tab.model";
import { CriteriaBuilder } from "../filter/criteria-builder.model";
import { Restrictions } from "../filter/restrictions.factory";

@Injectable({
  providedIn: "root",
})
export class WindowService {
  constructor(
    private poService: POService,
    private dbService: NgxIndexedDBService
  ) {}

  getWindowPO(windowID: number): Promise<WindowModel> {
    return new Promise<WindowModel>((resolve) => {
      let windowModel: WindowModel = null;
      //Get Table Info
      this.dbService
        .getByKey(WindowModel.tableName, windowID)
        .subscribe((windowData) => {
          windowModel = new WindowModel();
          if (windowData) {
            windowModel.loadFromJSON(windowData);

            this.dbService
              .getAllByIndex(TabModel.tableName,"AD_Window_ID",IDBKeyRange.only(windowID))
              .subscribe((tabData) => {
                if (tabData) {
                  let tabs: TabModel[] = [];
                  let keys: any[] = tabData;
                  keys.forEach((tabJSON) => {
                    let tabModel = new TabModel();
                    tabModel.loadFromJSON(tabJSON);
                    tabs.push(tabModel);
                  });

                  if (tabs) {
                    tabs.sort((a: TabModel, b: TabModel) => {
                      return a.seqNo - b.seqNo;
                    });
                    windowModel._tabs = tabs;
                  }
                }

                resolve(windowModel);
              });
          } else {
            this.poService
              .getPO(WindowModel.tableName, windowID, true)
              .toPromise()
              .then((data: any) => {
                //Load table Info
                windowModel.loadFromJSON(data);

                this.dbService.add(WindowModel.tableName, {
                  AD_Window_ID: data['AD_Window_ID'],
                  Name: data['Name'],
                  Description: data['Description'],
                }).subscribe((key) => {
                  console.log('key: ', key);
                });;


                //Get tabs
                let filter = new CriteriaBuilder()
                  .from(TabModel.tableName)
                  .addCriterion(Restrictions.eq("AD_Window_ID", windowID))
                  .build();

                this.poService
                  .getTableData(filter, true)
                  .toPromise()
                  .then((resp: any) => {
                    let data = resp.value;
                    if (data) {
                      let keys: any[] = data;
                      let tabs: TabModel[] = [];

                      keys.forEach((tabJSON) => {
                        let tabModel = new TabModel();
                        tabModel.loadFromJSON(tabJSON);

                        this.dbService.add(TabModel.tableName, {
                          AD_Tab_ID: tabJSON['AD_Tab_ID'],
                          AD_Window_ID: tabJSON['AD_Window_ID'],
                          SeqNo: tabJSON['SeqNo'],
                          IsReadOnly: tabJSON['IsReadOnly'],
                          Name: tabJSON['Name'],
                          Description: tabJSON['Description'],
                          TabLevel: tabJSON['TabLevel'],
                          IsInsertRecord: tabJSON['IsInsertRecord'],
                          Parent_Column_ID: tabJSON['Parent_Column_ID'],
                          Parent_ColumnName: tabJSON['Parent_ColumnName'],
                          OrderByClause: tabJSON['OrderByClause'],
                          AD_Table_ID: tabJSON['AD_Table_ID'],


                        }).subscribe((key) => {
                          console.log('key: ', key);
                        });;


                        tabs.push(tabModel);
                      });

                      if (tabs) {
                        tabs.sort((a: TabModel, b: TabModel) => {
                          return a.seqNo - b.seqNo;
                        });
                        windowModel._tabs = tabs;
                      }
                    }
                    resolve(windowModel);
                  });
              });
          }
        });
    });
  }
}
