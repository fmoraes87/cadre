import { FormGroup } from '@angular/forms';

export interface WebComponent {
    field: any;
    group: FormGroup;
    currentValue;
}
