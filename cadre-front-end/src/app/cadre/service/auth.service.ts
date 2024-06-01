import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, tap } from 'rxjs/operators';
import { throwError, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

import { NgxIndexedDBService } from 'ngx-indexed-db';
import { environment } from 'src/environments/environment';
import { UserModel } from '../domain/user.model';
import { AuthResponseData } from '../domain/auth-response-data';
import { Env } from '../util/env';

@Injectable({
    providedIn: 'root',
})
export class AuthService {

    private static readonly CLIENT_ID=environment.clientId;
    private static readonly CLIENT_SECRET=environment.clientSecret;

    static readonly LOCAL_STORAGE_USER_DATA = 'userData';


    private tokenExpirationTimer: any;

    user = new BehaviorSubject<UserModel>(null);

    constructor(private http: HttpClient, private router: Router,  private dbService: NgxIndexedDBService) { }

    autoLogin() {
        const userData: {
            email: string,
            ad_user_id: number ,
            ad_org_id: number,
            ad_client_id: number,
            ad_tree_id: number,
            _token: string,
            _refreshToken: string,
            _tokenExpirationDate: string
        } = JSON.parse(localStorage.getItem(AuthService.LOCAL_STORAGE_USER_DATA));

        if (userData) {
            const expirationDate = new Date(userData._tokenExpirationDate);
            const loadUser = new UserModel(
                userData.email
                , userData._token
                , userData._refreshToken
                , expirationDate
                , +userData.ad_user_id
                , +userData.ad_client_id
                , +userData.ad_org_id
                , +userData.ad_tree_id
            );

            if (loadUser.token){
                this.user.next(loadUser);
                this.autoLogout(expirationDate.getTime() - new Date().getTime());
                this.router.navigate(['/']);
            }

        }
    }

    autoLogout(expirationDuration: number){
        this.tokenExpirationTimer = setTimeout(()=>{
            this.logout();
            this.router.navigate(['/login']);
        },expirationDuration);
    }

   /* singup(email: string, password: string) {
        let url = 'https://www.googleapis.com/identitytoolkit/v3/relyingparty/signupNewUser?key=' + AuthService.API_KEY;

        return this.http.post<AuthResponseData>(url, {
            email: email,
            password: password,
            returnSecureToken: true

        }).pipe(catchError(this.handleError), tap(resp => {
            this.handleAuthentication(
                resp.email, resp.localId, resp.idToken, +resp.expiresIn
            );
        }));
    }*/

    login(email: string, password: string) {
        let url = environment.endpoint_token;
        let options = {
            headers: new HttpHeaders({
                'Content-Type':  'application/x-www-form-urlencoded',
                'Authorization':  'Basic ' + window.btoa(AuthService.CLIENT_ID + ':' + AuthService.CLIENT_SECRET)
            })
          };

        let body = new URLSearchParams();
        body.set('username', email);
        body.set('password', password);
        body.set('grant_type', 'password');
        body.set('provider', 'default');

        return this.http.post<AuthResponseData>(url, body.toString(), options).pipe(catchError(this.handleError), tap(resp => {
            this.handleAuthentication(
                email,resp
            );
        }));
    }

    logout() {
        localStorage.clear();
        sessionStorage.clear();

        //Clear all entities
        let entities: string [] = ['AD_Table',"AD_Column","AD_Window","AD_Tab","AD_Field"]
        entities.forEach(e=>{
            this.dbService.clear(e).subscribe((successDeleted) => {
                console.log('success? ', successDeleted);
              });

        })

        if (this.tokenExpirationTimer){
            clearTimeout(this.tokenExpirationTimer);
        }

        this.tokenExpirationTimer = null;

        this.user.next(null);

        this.router.navigate(['/login']);
    }

    private handleAuthentication(email: string, resp: AuthResponseData ) {
        //Extra + convert to a number
        const expirationDate = new Date(new Date().getTime() + (+resp.expires_in) * 1000);
        const user = new UserModel(
            email,
            resp.access_token,
            resp.refresh_token,
            expirationDate,
            +resp.ad_user_id,
            +resp.ad_client_id,
            +resp.ad_org_id,
            +resp.ad_tree_id
            );

        this.setGlobalValues(user);
        this.user.next(user);
        this.autoLogout((+resp.expires_in) * 1000);
        localStorage.setItem(AuthService.LOCAL_STORAGE_USER_DATA, JSON.stringify(user));



    }

    private setGlobalValues(user: UserModel){

        if (user){
            Env.setGlobalContext(Env.AD_USER_ID, user.ad_user_id);
            Env.setGlobalContext(Env.AD_ORG_ID, user.ad_org_id);
            Env.setGlobalContext(Env.AD_CLIENT_ID, user.ad_client_id);
            Env.setGlobalContext(Env.AD_MENU_ID, user.ad_tree_id);
        }else{
            Env.removeGlobalItem(Env.AD_USER_ID);
            Env.removeGlobalItem(Env.AD_ORG_ID);
            Env.removeGlobalItem(Env.AD_CLIENT_ID);
            Env.removeGlobalItem(Env.AD_MENU_ID);

        }

    }

    private handleError(errorResponse: HttpErrorResponse) {
      let errorMessage = 'An unknow error occurred!';
      if (errorResponse.error && errorResponse.message) {
          switch (errorResponse.error.message) {

              case 'AccountNotVerified':
                  errorMessage = 'The user account isnt verified.'; break;
              case 'Unauthorized':
                  errorMessage = 'Invalid credentials.'; break;
          }
      }

      return throwError(errorMessage);
    }
}
