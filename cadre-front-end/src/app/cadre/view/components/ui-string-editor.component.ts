import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FieldModel } from '../../domain/field.model';
import { WebComponent } from './web.component';
import {InputTextModule} from 'primeng/inputtext';

@Component({
  template: `
  <div [formGroup]="group">
    <label [title]="field.columnName" [for]="field.ad_field_id">{{field.label}}</label>
    <input type="text" pInputText
      [formControlName]="field.columnName"
      [ngClass]="customClass"
      [id]="field.ad_field_id"
      [value]="currentValue"
      [readonly]="field.isreadonly"
      [placeholder]="field.placeholder">
    </div>
  `
})
export class UIStringEditorComponent implements WebComponent {
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
