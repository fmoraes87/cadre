import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[web-component]',
})
export class WebDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}
