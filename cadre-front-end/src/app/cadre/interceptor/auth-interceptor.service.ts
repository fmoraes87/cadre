import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpHeaders } from '@angular/common/http';
import { exhaustMap, take } from 'rxjs/operators';
import { AuthService } from '../service/auth.service';
import { environment } from 'src/environments/environment';
import { Env } from '../util/env';


export const InterceptorAppHeader = 'X-App-Interceptor';

@Injectable()
export class AuthInterceptorService implements HttpInterceptor {

    constructor(private authService: AuthService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler) {


        if (req.headers.has(InterceptorAppHeader)) {
            // set token here.
            const headers = req.headers.delete(InterceptorAppHeader);
            let modifiedReq;

            let options = {
                headers: new HttpHeaders({
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Authorization': 'Basic ' + window.btoa(environment.clientId + ':' + environment.clientSecret)
                })
            };

            modifiedReq = req.clone(options);
            // Handle response
            return next.handle(modifiedReq);

        }else {
            // do
            return this.authService.user.pipe(
                take(1),
                exhaustMap(user => {
                    if (user && user.token) {
                        const modifiedReq = req.clone({
                            setHeaders: {
                                Authorization: user.token,
                                p_language: Env.getGlobalContext(Env.AD_LANUAGE_ID) ?
                                Env.getGlobalContext(Env.AD_LANUAGE_ID):
                                'en_US'
                            }

                        });

                        return next.handle(modifiedReq);
                    } else {
                        return next.handle(req);
                    }
                })
            );
        }

    }
}
