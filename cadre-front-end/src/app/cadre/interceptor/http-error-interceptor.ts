import {
    HTTP_INTERCEPTORS, HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor,
    HttpRequest
} from "@angular/common/http";
import {Injectable} from "@angular/core";

import {Router} from "@angular/router";
import { MessageService } from "primeng/api";
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from "../service/auth.service";

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {

    constructor(private router: Router,
        private messageService: MessageService,
        private authService: AuthService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(catchError(error => {
            if (error instanceof HttpErrorResponse) {
                const errorResponseData: {
                    code: string,
                    message: string,
                } = error.error;

                if (error.status === 401){
                    this.messageService.add({severity: 'error', summary: 'Error', detail: 'Access Unauthorized'});
                    this.authService.logout();
                    //this.messageService.publicInfoMessage('Error ocurred:'+ errorResponseData.message);
                    this.router.navigate(['/auth'], { queryParams: { isLoginMode: 'true' } });
                }
                else{
                    if (errorResponseData){
                        if (errorResponseData.message){
                            this.messageService.add({severity: 'error', summary: 'Error', detail:errorResponseData.message});
                        }else{
                            this.messageService.add({severity: 'error', summary: 'Error', detail:errorResponseData.code});

                        }
                    }else{
                        this.messageService.add({severity: 'error', summary: 'Error', detail:error.message});
                    }
                }

            }
            return throwError(error);
        }));

    }

}

export const HttpErrorInterceptorProvider = {
    provide: HTTP_INTERCEPTORS,
    useClass: HttpErrorInterceptor,
    multi: true,
};
