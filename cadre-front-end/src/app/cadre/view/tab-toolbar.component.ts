import { Component, ComponentFactoryResolver, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { ConfirmationService } from 'primeng/api';
import { ToolBarButton } from '../domain/toolbarbutton.model ';
import { GridTab } from '../service/gridtab.service.';
import { TabCustomButtonComponent } from './tab-custom-button.component';

@Component({
  selector: 'app-tab-toolbar',
  templateUrl: "./tab-toolbar.component.html",
  styles: [`
  :host ::ng-deep button {
      margin-right: .25em;
      margin-left: .25em;
  }

  :host ::ng-deep .p-splitbutton button {
      margin-right: 0;
      margin-left: 0;
  }

  @media screen and (max-width: 960px) {
      .card.toolbar-demo {
          overflow: auto;
      }
  }
`],
  providers: [ConfirmationService]

})
export class TabToolbarComponent implements OnInit {

  displayImportDialog: boolean = false;

  constructor(private componentFactoryResolver: ComponentFactoryResolver,
     private confirmationService: ConfirmationService,
      private gridTab: GridTab,) { }

  ngOnInit(): void {
  }

  get table(){
    return this.gridTab._table;
  }

  get customToolBarButtons(){
    return this.gridTab._customToolBarButtons;
  }

  get searchEnabled(){
    return this.gridTab._searchEnabled;
  }

  get serchMode(){
    return this.gridTab._serchMode;
  }

  onClickButton(value) {
    if (value=='save' || value=='delete' ||  value=='duplicate' || value=='undo'){
      this.confirmationService.confirm({
        message: 'Are you sure that you want to perform this action?',
        accept: () => {
          this.gridTab.onClickButton(value);

        }
      });
    }else{
      this.gridTab.onClickButton(value);

    }
  }

  enableButton(value): boolean {
    return this.gridTab.enableButton(value)
  }

  @ViewChild('vc', {read: ViewContainerRef}) vc: ViewContainerRef;
  private _customToolBarModal;


  executeCustomAction(customButton: ToolBarButton) {

    let componentFactory = this.componentFactoryResolver.resolveComponentFactory(TabCustomButtonComponent);

    this.vc.clear();

    this._customToolBarModal = this.vc.createComponent(componentFactory);
    (<TabCustomButtonComponent>this._customToolBarModal.instance).process(customButton.ad_process_id,customButton.process_value,customButton.action_name);

  }

  openImportFileDialog(){
    this.displayImportDialog=true;
  }

}
