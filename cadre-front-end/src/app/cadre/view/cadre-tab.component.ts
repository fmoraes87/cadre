import { Component, ComponentFactoryResolver, Input, OnDestroy, OnInit, ViewChild, ViewContainerRef } from "@angular/core";
import { TabModel } from "../domain/tab.model";
import { GridTab } from "../service/gridtab.service.";
import { Env } from "../util/env";

@Component({
  selector: "app-cadre-tab",
  templateUrl: "./cadre-tab.component.html",
  providers: [GridTab]
})
export class CadreTabComponent implements OnInit, OnDestroy {
  /* the record represents the Tab */
  @Input() currentTabModel: TabModel;

  /* tab is selected*/
  @Input() selected: boolean;

  /* tab position*/
  @Input() currentLevel: number;


  constructor(
    private gridTab: GridTab
  ) {}

  ngOnInit() {
    if (this.selected) {
      this.initTab(true);
    }
  }

  ngOnDestroy() {
    Env.clearAll();
  }

  /**
   * Loaded tab according to database
   */
  initTab(reload: boolean) {
    this.gridTab.initTab(reload,this.currentLevel,this.currentTabModel);  
  }

  get tabFormGroup(){
    return this.gridTab.tabFormGroup;
  }



}
