import { Component, ComponentFactoryResolver, Input,OnDestroy,OnInit, ViewChild } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';

import { Subscription } from 'rxjs';
import { DisplayType } from '../domain/display-tpe.constant';
import { FieldModel } from '../domain/field.model';
import { POModel } from '../domain/po.model';
import { GridTab } from '../service/gridtab.service.';
import { Parser } from '../util/parser';
import { DefaultEditorFactory } from './components/web-editor-factory';
import { WebComponent } from './components/web.component';
import { WebDirective } from './components/web.directive';



@Component({
  selector: 'app-field',
  template: `
  <ng-template web-component></ng-template>
  `
})
export class FieldComponent implements OnDestroy,OnInit {

  @Input() field: FieldModel;
  @Input() formGroup: FormGroup;

  @ViewChild(WebDirective, {static: true}) ngTemplate: WebDirective;

  private _componentRef;

  modelChangedSubscription: Subscription;

  constructor(private gridTab: GridTab
    ,private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit() {
    this.loadComponent(null);

    this.modelChangedSubscription = this.gridTab.selectedModelChanged.subscribe(
      (model: POModel) => {

        let formControl = this.formGroup.get(this.field.columnName);
        if (formControl){
          if (model){
            let clone = Object.create(model);
            let currentValue = clone.getValueByColumnName(this.field.columnName);
            let componentValue = this.getValue(currentValue,this.field.defaultvalue);
            formControl.setValue(componentValue);
            this.loadComponent(componentValue);
          }else{
            formControl.setValue(this.field.defaultvalue);
            this.loadComponent(this.field.defaultvalue);
          }

          if (this.field.ismandatory){
            formControl.setValidators([Validators.required])
            formControl.markAsDirty();
          }
        }
      }

    );

  }

  getValue(currentValue,defaultValue){
    let result = currentValue;
    if (typeof currentValue == 'undefined' || currentValue==null){
      if (this.field.ad_reference_id===18 || 19 || 20){
        if ((typeof defaultValue === 'string' || defaultValue instanceof String)
            && defaultValue.startsWith("@")
            && defaultValue.endsWith("@")
            && defaultValue.length > 2 ) {
              let variable = defaultValue.substring(1,defaultValue.length -1);
              result= Parser.parseVariable(variable);
        }else{
          result = defaultValue;
        }
      }
    }else{
      result=currentValue;
    }

    if (result !=null){
       if (DisplayType.isNumeric(this.field.ad_reference_id)){ //Numeric
         result = +result;
       }else if (DisplayType.isDate(this.field.ad_reference_id)){ //Date - DateTime
         let d=  Date.parse(result)
         let dateValue = new Date(d);
          let currentTimeZone = new Date().getTimezoneOffset();
         dateValue.setTime(dateValue.getTime() + (-1*currentTimeZone)*60*1000);


         result=dateValue;
          //let isoString = dateValue.toISOString()
          //result=isoString.substring(0, (isoString.indexOf("T")|0) + 6|0);
        }

      }

      return result;

  }

  loadComponent(currentValue) {


    if (!this._componentRef || this.field.dynamic_validation){

          let component = DefaultEditorFactory.getElement(this.field.ad_reference_id);

          let viewContainerRef = this.ngTemplate.viewContainerRef;
          viewContainerRef.clear();

          if (component){

            let componentFactory = this.componentFactoryResolver.resolveComponentFactory(component);

            this._componentRef = viewContainerRef.createComponent(componentFactory);
          }

    }

    if (this._componentRef){
      (<WebComponent>this._componentRef.instance).field = this.field;
      (<WebComponent>this._componentRef.instance).group = this.formGroup;
      (<WebComponent>this._componentRef.instance).currentValue = currentValue;

    }

  }

  ngOnDestroy(){
    if (this.modelChangedSubscription){
      this.modelChangedSubscription.unsubscribe();
    }

  }

}
