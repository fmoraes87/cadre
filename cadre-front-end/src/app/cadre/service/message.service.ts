import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
    providedIn: 'root',
  })
export class MessageService{

    //Messages
    _success = new Subject<string>();
    _error = new Subject<string>();
    _info = new Subject<string>();

    public publicSuccessMessage(message: string) {
        this._success.next(message);
    }

    public publicErrorMessage(message: string) {
        this._error.next(message);
    }

    public publicInfoMessage(message: string) {
        this._info.next(message);
    }

}
