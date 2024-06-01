import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FieldModel } from '../../domain/field.model';
import { WebComponent } from './web.component';

@Component({
  template: `
  <div [formGroup]="group">
    <label [title]="field.columnName" [for]="field.ad_field_id">{{field.label}}</label>
    <p-inputNumber
      mode="decimal"
      [formControlName]="field.columnName"
      [showButtons]="true"
      [inputId]="field.ad_field_id"
      [ngClass]="customClass"
      [placeholder]="field.placeholder"
      currency="BRL" locale="pt-BR"
      [min]="0" [max]="100">
			</p-inputNumber>
    </div>
  `
})
export class UIAmountEditorComponent implements WebComponent {
  @Input() field: FieldModel;
  @Input() group: FormGroup;
  @Input() currentValue;

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
