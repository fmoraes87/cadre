import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FieldModel } from '../../domain/field.model';
import { WebComponent } from './web.component';

@Component({
  template: `
  <div [formGroup]="group" class="dynamic-field form-select">
    <label [title]="field.columnName" [for]="field.ad_field_id">{{field.label}}</label>
    <input type="password" pPassword
      [formControlName]="field.columnName"
      [id]="field.ad_field_id"
      [ngClass]="customClass"
      [readonly]="field.isreadonly"
      [placeholder]="field.placeholder">
    </div>
  `
})
export class UIPasswordEditorComponent implements WebComponent {
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
