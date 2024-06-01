import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { NgxIndexedDBService } from 'ngx-indexed-db';
import { FieldModel } from '../../domain/field.model';
import { TableModel } from '../../domain/table.model';
import { ValueNamePair } from '../../domain/value-name-pair.model';
import { CriteriaBuilder } from '../../filter/criteria-builder.model';
import { CustomExpression } from '../../filter/custom-expression.model';
import { Restrictions } from '../../filter/restrictions.factory';
import { POService } from '../../service/po.service';
import { TableService } from '../../service/table.service';
import { Parser } from '../../util/parser';
import { WebComponent } from './web.component';



@Component({
  selector: 'app-w-table-direct-editor',
  template: `
   <div [formGroup]="group">
    <label  [for]="field.ad_field_id">{{field.label}}</label>
    <p-dropdown [inputId]="field.ad_field_id"
      [options]="list"
      placeholder="Select"
      [formControlName]="field.columnName"
      [ngClass]="customClass"
      [readonly]="field.isreadonly"
      optionLabel="name"
      optionValue="value">
    </p-dropdown>
   </div>
  `
})
export class UITableDirectEditorComponent implements OnInit, WebComponent {
  @Input() field: FieldModel;
  @Input() group: FormGroup;
  @Input() currentValue;

  list: ValueNamePair[] = [];

  constructor(private poService: POService, private tableService: TableService) { }

  ngOnInit() {
    this.list.push(ValueNamePair.EMPTY);

    let tableName: string = this.field.columnName.substr(0, this.field.columnName.length - 3);

    let filter = new CriteriaBuilder()
    .select([TableModel.COLUMNNAME_AD_Table_ID])
    .from(TableModel.tableName)
    .addCriterion(Restrictions.eq(TableModel.COLUMNNAME_TableName, tableName))
    .build();

    this.poService
    .getValueEx(filter, true)
    .toPromise()
    .then((data: any) => {

      let tableId = data[0][TableModel.COLUMNNAME_AD_Table_ID];
      this.tableService.getTableById(tableId).then((tableModel) => {
        let identifiers: string[] = tableModel.getIdentifiers();

        let tableName = tableModel.tableName;

        let dynamicValidation = Parser.parseFullExpression(
          this.field.dynamic_validation
        );

        let criteriaBuilder = new CriteriaBuilder().from(tableName).select(identifiers);

        if (dynamicValidation) {
          criteriaBuilder.addCriterion(
            new CustomExpression(dynamicValidation)
          );
        }

        filter = criteriaBuilder.build();

        this.poService
          .getTableData(filter, false)
          .toPromise()
          .then((data: any) => {
            if (data && data.value) {
              this.list = this.list.concat(
                this.createList(data.value,tableName + "_ID",identifiers)
              );
            }
          });
      });


    });

  }

  createList(jsonData: any, keyIdentifier: string, columnsIdentifier: string []): ValueNamePair[] {
    let modelList: ValueNamePair[] = [];
    if (jsonData) {
      let keys: any[] = Object.keys(jsonData);

      keys.forEach(currentKey => {
        let element = jsonData[currentKey];

        let elementValue;
        if (columnsIdentifier && columnsIdentifier.length > 0){
          columnsIdentifier.forEach(columnName =>{

            elementValue= elementValue? elementValue + ' - ' + element[columnName]: element[columnName]
          })

        }else{
          elementValue= element["Value"]
          ? element["Value"]
          : element["Name"];
        }

        modelList.push(
          new ValueNamePair(
            element[keyIdentifier],//key
            elementValue
          )
        );
      });
    }

    return modelList;
  }

  get formControl(){
    return this.group[this.field.columnName];
  }

  get invalid(){
    let formControl = this.formControl;
    return !formControl || !formControl.valid
  }

  get customClass(){
    if (!this.invalid){
      return "";
    }else{
      return "ng-invalid ng-dirty"
    }
  }

}
