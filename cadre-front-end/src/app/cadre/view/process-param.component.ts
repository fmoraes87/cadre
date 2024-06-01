import { Component, ComponentFactoryResolver, Input,OnDestroy,OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { Subscription } from 'rxjs';
import { DisplayType } from '../domain/display-tpe.constant';
import { ProcessParamModel } from '../domain/process-param.model';
import { Env } from '../util/env';
import { Parser } from '../util/parser';
import { DefaultEditorFactory } from './components/web-editor-factory';
import { WebComponent } from './components/web.component';
import { WebDirective } from './components/web.directive';


@Component({
  selector: 'app-process-para',
  template: `
  <ng-template web-component></ng-template>
  `
})
export class ProcessParamComponent implements OnDestroy,OnInit {

  @Input() processPara: ProcessParamModel;
  @Input() formGroup: FormGroup;

  @ViewChild(WebDirective, {static: true}) ngTemplate: WebDirective;

  private _componentRef;

  modelChangedSubscription: Subscription;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit() {
    this.loadComponent(null);

    let formControl = this.formGroup.get(this.processPara.columnName);
    let componentValue = this.getValue(this.getCurrentValue(this.processPara.columnName),this.processPara.defaultvalue);

    formControl.setValue(componentValue);
    this.loadComponent(componentValue);

  }

  getCurrentValue(paramName){
    return Env.getWindowContext(
      false,
      Parser.parseVariable("$AD_Window_ID"),
      Parser.parseVariable("$AD_Tab_ID"),
      paramName);
  }

  getValue(currentValue,defaultValue){
    let result = currentValue;
    if (typeof currentValue == 'undefined' || currentValue==null){
      if (this.processPara.ad_reference_id===18 || 19 || 20){
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
       if (DisplayType.isNumeric(this.processPara.ad_reference_id)){ //Numeric
         result = +result;
       }else if (this.processPara.ad_reference_id == DisplayType.DateTime){ //Date - DateTime
         let d=  Date.parse(result)
         let dateValue = new Date(d);
          let currentTimeZone = new Date().getTimezoneOffset();
         dateValue.setTime(dateValue.getTime() + (-1*currentTimeZone)*60*1000);

          let isoString = dateValue.toISOString()
          result=isoString.substring(0, (isoString.indexOf("T")|0) + 6|0);
        }

      }

      return result;

  }

  loadComponent(currentValue) {

    if (!this._componentRef){

          let component = DefaultEditorFactory.getElement(this.processPara.ad_reference_id);

          let componentFactory = this.componentFactoryResolver.resolveComponentFactory(component);

          let viewContainerRef = this.ngTemplate.viewContainerRef;
          viewContainerRef.clear();

          this._componentRef = viewContainerRef.createComponent(componentFactory);
    }

    (<WebComponent>this._componentRef.instance).field = this.processPara;
    (<WebComponent>this._componentRef.instance).group = this.formGroup;
    (<WebComponent>this._componentRef.instance).currentValue = currentValue;

  }

  ngOnDestroy(){
    if (this.modelChangedSubscription){
      this.modelChangedSubscription.unsubscribe();
    }

  }

}
