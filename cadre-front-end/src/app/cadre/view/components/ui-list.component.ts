import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FieldModel } from '../../domain/field.model';
import { RefListModel } from '../../domain/ref_list.model';
import { ValueNamePair } from '../../domain/value-name-pair.model';
import { CriteriaBuilder } from '../../filter/criteria-builder.model';
import { Restrictions } from '../../filter/restrictions.factory';
import { POService } from '../../service/po.service';
import { WebComponent } from './web.component';


@Component({
  selector: 'app-w-list-editor',
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
  `
})
export class UIListComponent implements OnInit, WebComponent {
  @Input() field: FieldModel;
  @Input() group: FormGroup;
  @Input() currentValue;

  list: ValueNamePair[] = [];

  constructor(private poService: POService) { }

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

  ngOnInit() {
    this.list.push(ValueNamePair.EMPTY);


    let filter = new CriteriaBuilder()
      .from(RefListModel.tableName)
      .addCriterion(Restrictions.eq("AD_Reference_ID", this.field.ad_reference_value_id))
      .build();

    this.poService.getTableData(filter,false)
      .subscribe(
        (data: any) => {
          if (data && data.value) {
            this.list = this.list.concat(this.createList('Value', data.value));
          }

        }
      )
  }

  createList(keyIdentifier: string, jsonData: any): ValueNamePair[] {
    let modelList: ValueNamePair[] = [];
    if (jsonData) {
      let list: any[] = [];
      let keys: any[] = Object.keys(jsonData);

      keys.forEach(currentKey => {
        let element = jsonData[currentKey];
         //TODO- should evaluate isIdentifier
        let elementValue = element['Name'] ? element['Name'] : element['Value']
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

}
