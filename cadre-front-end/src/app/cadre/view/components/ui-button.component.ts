import { Component, ComponentFactoryResolver, ElementRef, Input, ViewChild, ViewContainerRef } from "@angular/core";
import { FormGroup } from "@angular/forms";

import { FieldModel } from '../../domain/field.model';
import { TabCustomButtonComponent } from "../tab-custom-button.component";
import { WebComponent } from './web.component';

@Component({
  template: `
    <ng-container #vc></ng-container>
    <div>
      <button pButton pRipple
        type="button"
        class="p-button-warning"
        [title]="field.label"
        (click)="executeCustomAction()"
        [disabled]="field.isreadonly"
        [label]="field.label"
        data-toggle="modal" data-target="#customButtonAction" data-backdrop="false"
      >

      </button>
    </div>
  `,
})
export class UIButton implements WebComponent {
  @Input() field: FieldModel;
  @Input() group: FormGroup;
  @Input() currentValue;

  @ViewChild("fileInput") fileInput: ElementRef;

  @ViewChild('vc', {read: ViewContainerRef}) vc: ViewContainerRef;
  private _customToolBarModal;

  constructor(
    private componentFactoryResolver: ComponentFactoryResolver
  ) {}

  executeCustomAction() {

    let componentFactory = this.componentFactoryResolver.resolveComponentFactory(TabCustomButtonComponent);

    this.vc.clear();

    this._customToolBarModal = this.vc.createComponent(componentFactory);
    (<TabCustomButtonComponent>this._customToolBarModal.instance).process(this.field.ad_process_id,this.field.process_value, this.field.label);

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
