import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { CEvent } from '../domain/custom-event.interface';

@Injectable({
    providedIn: 'root',
  })
export class EventService{

    //_event
    _event = new Subject<CEvent>();

    public sendEvent(event: CEvent) {
        this._event.next(event);
    }

}
