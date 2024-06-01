import { Component, Input, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { ProcessParamModel } from "../domain/process-param.model";
import { GridTab } from "../service/gridtab.service.";
import { POService } from "../service/po.service";

@Component({
  selector: "app-tab-custom-button",
  template: `
    <!-- Modal -->
    <!--<div *ngIf="button">-->
    <p-dialog [header]="actionName" [(visible)]="display" >
      <form [formGroup]="formGroup" novalidate (ngSubmit)="onSubmit()">
       <div class="p-grid">
          <div class="p-col-12">
            <div class="card">
              <div class="p-fluid p-formgrid p-grid">
                <div class="form-group" style="width: 100%">
                  <ng-template
                    ngFor
                    let-paramGroup
                    [ngForOf]="_paramsGroups"
                    let-i="index"
                  >
                    <div class="row">
                      <app-process-para
                        *ngFor="let param of paramGroup"
                        [formGroup]="formGroup"
                        [processPara]="param"
                        [class]="param.boostrapClass"
                      >
                      </app-process-para>
                    </div>
                  </ng-template>

                  <label for="exampleFormControlFile1" style="color:red">{{
                    _errorMessage
                  }}</label>
                </div>
              </div>
            </div>
            <div>
              <button pButton
                type="button"
                [disabled]="formGroup.invalid"
                class="p-button-warning"
                type="submit" label="Confirm"
              ></button>
            </div>
          </div>
        </div>
      </form>
    </p-dialog>
  `,
  styles: [],
})
export class TabCustomButtonComponent implements OnInit {
  display: boolean = true;
  adProcessID: number;
  processValue: string;
  actionName: string;

  _errorMessage = "";

  _paramsGroups: [][];

  formGroup = this.fb.group({});

  params: ProcessParamModel[] = [];

  // I initialize the app component.
  constructor(
    private fb: FormBuilder,
    private gridTab: GridTab,
    private poService: POService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {}

  @Input()
  process(adProcessID, processValue, actionName) {
    this.adProcessID = adProcessID;
    this.processValue = processValue;
    this.actionName = actionName;
    this.gridTab
      .loadProcessParams(this.adProcessID)
      .then((resp: ProcessParamModel[]) => {
        this.params = resp;
        let group: any = {};
        this.params.forEach((element) => {
          group[element.columnName] = new FormControl("");
        });
        this.formGroup = new FormGroup(group);
        this.loadParamGroups();
      });
  }

  onSubmit() {
    this.poService
      .executeOdataProcess(this.processValue, this.formGroup.value, false)
      .subscribe((data: any) => {
        this.messageService.add({severity:'success', summary:'Success', detail:data.value});
      });

    //this.formGroup.reset();
  }

  loadParamGroups() {
    if (!this._paramsGroups) {
      let paramsGroups = [];
      let paramGroupIndex: number = -1;
      let paramsLength = this.params.length;

      for (var i: number = 0; i < paramsLength; i++) {
        let paramModel = this.params[i];

        //if is the first record or is not in the same line
        //create new group
        if (i == 0 || !paramModel.issameline) {
          paramGroupIndex++;
          paramsGroups[paramGroupIndex] = [];
        }

        paramsGroups[paramGroupIndex].push(paramModel);
      }

      this._paramsGroups = paramsGroups;
    }
  }

  closeModal() {
    this.params = null;
  }
}
