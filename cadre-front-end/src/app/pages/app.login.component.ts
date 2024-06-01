import {Component, OnInit} from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import {AppComponent} from '../app.component';
import { AuthResponseData } from '../cadre/domain/auth-response-data';
import { AuthService } from '../cadre/service/auth.service';

@Component({
    selector: 'app-login',
    templateUrl: './app.login.component.html'
})
export class AppLoginComponent implements OnInit {

    isLoginMode: boolean = true;
    isLoading: boolean = false;
    error: string = null;


  form: FormGroup;
  email = new FormControl("", Validators.compose([Validators.required, Validators.email]));
  password = new FormControl("",
    Validators.compose([
      Validators.required
      , Validators.minLength(6)
    ])
  );

  constructor(public app: AppComponent, private fb: FormBuilder,
    private authService: AuthService,
    private router: Router) { }

  ngOnInit() {
    this.form = this.fb.group({
      "email": this.email,
      "password": this.password
    });
  }


  onSwitchMode() {
    this.isLoginMode = !this.isLoginMode;
  }

  onSubmit() {
    let obsAuth$: Observable<AuthResponseData>;

    this.isLoading = true;
    if (this.isLoginMode) {
      obsAuth$ = this.authService.login(this.email.value, this.password.value);
    }/* else {
      obsAuth$ = this.authService.singup(this.email.value, this.password.value);
    }*/

    obsAuth$.subscribe(responseData => {
      this.isLoading = false;
      this.router.navigate(['/']);
    }, errorMessage => {
      this.error = errorMessage;
      this.isLoading = false;
    });
    this.form.reset();
  }

}
