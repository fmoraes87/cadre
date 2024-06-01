import { Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { BreadcrumbService } from 'src/app/app.breadcrumb.service';
import { TabModel } from '../domain/tab.model';
import { WindowService } from '../service/window.service';
import { Env } from '../util/env';
import { CadreTabComponent } from './cadre-tab.component';

@Component({
  selector: 'app-window',
  templateUrl: './window.component.html'})
export class WindowComponent implements OnInit, OnDestroy {

  windowId: number;
  windowName: string;
  tabs :  TabModel [] ;

  selectedIndex: number = 0;
  loading: boolean = false;

  @ViewChildren(CadreTabComponent) tabsComponent: QueryList<CadreTabComponent>


  constructor(private route: ActivatedRoute
    ,private breadcrumbService: BreadcrumbService
    ,private windowService: WindowService) { }

  ngOnInit(): void {
    Env.clearAll();

    this.windowId = +this.route.snapshot.params['id'];
    this.loading = true;

    if (this.loading){
      this.route.params.subscribe(
        (params: Params) => {
          this.loading = true;
          this.windowId = +params['id'];
          this.windowService.getWindowPO(this.windowId)
            .then((windowModel) => {
                this.selectedIndex =0 ;
                this.windowName = windowModel.name;
                this.tabs = windowModel.tabs;
                this.loading = false;

                this.breadcrumbService.setItems([
                      {label: this.windowName, routerLink: ['/window/'+windowModel._id]}
                ]);
              });
        }
        );
    }

  }

  onSelectTab(e) {

   let oldSelectexIndex = this.selectedIndex;
   this.selectedIndex = e.index;

   for (var _i = 0; _i < this.tabsComponent.length; _i++) {
     let tabInstance = this.tabsComponent.toArray()[_i];
     tabInstance.selected = (_i==this.selectedIndex);
     if ( tabInstance.selected){
       let oldTabInstance = this.tabsComponent.toArray()[oldSelectexIndex];

       tabInstance.initTab(oldTabInstance.currentTabModel.tabLevel < tabInstance.currentTabModel.tabLevel);
     }
   }


}


  ngOnDestroy(){
    Env.clearAll();
  }

}
