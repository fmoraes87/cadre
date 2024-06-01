import { Injectable } from "@angular/core";
import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse,
} from "@angular/common/http";

import { Observable } from "rxjs";
import { map, share } from "rxjs/operators";
import { environment } from "src/environments/environment";
import { POModel } from "../domain/po.model";
import { TableModel } from "../domain/table.model";
import { Criteria } from "../filter/criteria.model";
import { InterceptorAppHeader } from "../interceptor/auth-interceptor.service";
import { GenericPO } from "../domain/generic.po.model";


@Injectable({
  providedIn: "root",
})
export class POService {
  public readonly ODATA_SERVICE: string = environment.endpoint_odata;

  constructor(private httpClient: HttpClient) {}

  //TODO - quando possibilitar sobrecarga ficara mais facil
  getModels<T extends POModel>(
    table: TableModel,
    criteria: Criteria,
    appRequest: boolean
  ): Observable<T[]> {
    let url = this.ODATA_SERVICE + table.tableName;
    if (criteria) {
      url += criteria.toQueryString();
    }

    let headers = new HttpHeaders();
    if (appRequest) {
      headers = headers.set(InterceptorAppHeader, "");
    }

    return this.httpClient
      .get<any>(url, { headers })
      .pipe(
        map((res) => {
          if (res) {
            const odataDataresponse = res;
            let records = odataDataresponse.value;
            let list: any[] = [];

            records.forEach((record) => {
              let model = new GenericPO(table);
              model.loadFromJSON(record);
              list.push(model);
            });

            return list;
          }
        }),
        share()
      );
  }

  getTableData(criteria: Criteria, appRequest: boolean) {
    let url = this.ODATA_SERVICE + criteria._resource;
    if (criteria) {
      url += criteria.toQueryString();
    }

    let headers = new HttpHeaders();
    if (appRequest) {
      headers = headers.set(InterceptorAppHeader, "");
    }

    return this.httpClient
      .get<any>(url, { headers })
      .pipe(
        map(
          (res) => {
            if (res) {
              return res;
            }
          },
          (error) => console.log(error)
        )
      );
  }

  /**
   * Get Single Value
   * @param tableName
   * @param columnName
   * @param criteria
   */
  getValueEx(criteria: Criteria, appRequest: boolean) {
    let url = this.ODATA_SERVICE + criteria._resource;
    url += criteria.toQueryString();

    let headers = new HttpHeaders();
    if (appRequest) {
      headers = headers.set(InterceptorAppHeader, "");
    }

    return this.httpClient
      .get<any>(url, { headers })
      .pipe(
        map((res) => {
          if (res) {
            const odataDataresponse = res;
            return odataDataresponse.value;
          }
        })
      );
  }

  getPO(tableName: string, id: number, appRequest: boolean) {
    let headers = new HttpHeaders();
    if (appRequest) {
      headers = headers.set(InterceptorAppHeader, "");
    }

    let url = this.ODATA_SERVICE + tableName + "(" + id + ")";
    return this.httpClient
      .get<any>(url, { headers })
      .pipe(
        map((res) => {
          const odataDataresponse = res;
          return odataDataresponse;
        })
      );
  }

  savePO<T extends POModel>(model: T, appRequest: boolean) {
    let headers = new HttpHeaders();
    if (appRequest) {
      headers = headers.set(InterceptorAppHeader, "");
    }

    if (model.is_New() || model.is_Changed()) {
      let tableName = model.poTableName;

      if (!model.is_New()) {
        let keyColumnValue = model.getKeyValue();

        return this.httpClient
          .put(
            this.ODATA_SERVICE + tableName + "(" + keyColumnValue + ")",
            model.getNewValuesASJSON(),
            { headers }
          )
          .pipe(
            map((response) => {
              return response;
            })
          );
      } else {
        return this.httpClient
          .post(this.ODATA_SERVICE + tableName, model.getJSON(), { headers })
          .pipe(
            map((response) => {
              return response;
            })
          );
      }
    }
  }

  delete<T extends POModel>(model: T, appRequest: boolean) {
    let headers = new HttpHeaders();
    if (appRequest) {
      headers = headers.set(InterceptorAppHeader, "");
    }

    if (!model.is_New()) {
      let tableName = model.poTableName;
      let tableColumnId = tableName + "_ID";
      let tableColumnIdValue = model.getValueByColumnName(tableColumnId);

      return this.httpClient
        .delete(
          this.ODATA_SERVICE + tableName + "(" + tableColumnIdValue + ")",
          { headers }
        )
        .pipe(
          map((response) => {
            return response;
          })
        );
    }
  }

  executeProcessByName(processName: string, params, appRequest: boolean) {
    let headers = new HttpHeaders();
    headers = headers.set("Content-Type", "application/x-www-form-urlencoded");
    if (appRequest) {
      headers = headers.set(InterceptorAppHeader, "");
    }

    return this.httpClient
      .post(
        environment.server + "/api/v1/process?processName=" + processName,
        params,
        { headers }
      )
      .pipe(
        map((response) => {
          return response;
        })
      );
  }

  executeOdataProcess(processId: string, params, appRequest: boolean) {
    let headers = new HttpHeaders();
    headers = headers.set('Content-Type', 'application/json');
    if (appRequest) {
      headers = headers.set(InterceptorAppHeader, "");
    }

    return this.httpClient
      .post(this.ODATA_SERVICE +'cadreAction_'+ processId,params, { headers })
      .pipe(
        map((response) => {
          return response;
        })
      );
  }
}
