import { Component, Input, OnInit } from "@angular/core";
import { FormGroup } from "@angular/forms";
import { FieldModel } from "../../domain/field.model";
import { ValueNamePair } from "../../domain/value-name-pair.model";
import { CriteriaBuilder } from "../../filter/criteria-builder.model";
import { CustomExpression } from "../../filter/custom-expression.model";
import { Restrictions } from "../../filter/restrictions.factory";
import { POService } from "../../service/po.service";
import { TableService } from "../../service/table.service";
import { Parser } from "../../util/parser";
import { WebComponent } from "./web.component";


@Component({
  selector: "app-w-table-editor",
  template: `
    <div [formGroup]="group">
      <label  [for]="field.ad_field_id">{{field.label}}</label>
      <p-dropdown [inputId]="field.ad_field_id"
        [options]="list"
        placeholder="Select"
        [ngClass]="customClass"
        [formControlName]="field.columnName"
        [readonly]="field.isreadonly"
        optionLabel="name"
        optionValue="value">
      </p-dropdown>
    </div>

  `,
})
export class UITableEditorComponent implements OnInit, WebComponent {
  @Input() field: FieldModel;
  @Input() group: FormGroup;
  @Input() currentValue;

  list: ValueNamePair[] = [];

  constructor(
    private poService: POService,
    private tableService: TableService
  ) {}

  ngOnInit() {
    this.list.push(ValueNamePair.EMPTY);

    let columnName = "AD_Table_ID";

    let filter = new CriteriaBuilder()
      .select([columnName])
      .from("AD_Reference")
      .addCriterion(
        Restrictions.eq("AD_Reference_ID", this.field.ad_reference_value_id)
      )
      .build();


    this.poService
      .getValueEx(filter, true)
      .toPromise()
      .then((data: any) => {
        let columnValue = data[0][columnName];

        this.tableService.getTableById(columnValue).then((tableModel) => {
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

  createList(jsonData: any, keyIdentifier: string, columnsIdentifier: string [] ): ValueNamePair[] {
    let modelList: ValueNamePair[] = [];
    if (jsonData) {
      let list: any[] = [];
      let keys: any[] = Object.keys(jsonData);

      keys.forEach((currentKey) => {
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
            element[keyIdentifier], //key
            elementValue
          )
        );
      });
    }

    return modelList;
  }
}
