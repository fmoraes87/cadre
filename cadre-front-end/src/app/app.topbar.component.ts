import {Component, OnDestroy} from '@angular/core';
import { AppComponent } from './app.component';
import { AppMainComponent } from './app.main.component';
import { BreadcrumbService } from './app.breadcrumb.service';
import { Subscription } from 'rxjs';
import { MenuItem } from 'primeng/api';
import { AuthService } from './cadre/service/auth.service';

@Component({
  selector: 'app-topbar',
  templateUrl: './app.topbar.component.html'
})
export class AppTopBarComponent implements OnDestroy{

    subscription: Subscription;

    items: MenuItem[];

    constructor(private authService: AuthService,
        public breadcrumbService: BreadcrumbService, public app: AppComponent, public appMain: AppMainComponent) {
        this.subscription = breadcrumbService.itemsHandler.subscribe(response => {
            this.items = response;
        });
    }

    ngOnDestroy() {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }

    onLogout() {
        this.authService.logout();
      }
}
