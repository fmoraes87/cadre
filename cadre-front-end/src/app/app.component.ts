import {Component, OnInit} from '@angular/core';
import {PrimeNGConfig} from 'primeng/api';
import { AuthService } from './cadre/service/auth.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    menuMode = 'slim';

    colorScheme = 'light';

    menuTheme = 'layout-sidebar-darkgray';

    inputStyle = 'filled';

    ripple: boolean;

    constructor(private authService: AuthService,
        private primengConfig: PrimeNGConfig) {
    }

    ngOnInit() {
        this.authService.autoLogin();
        this.primengConfig.ripple = true;
    }
}
