import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild , Input} from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { POService } from '../service/po.service';


@Component({
  selector: 'app-tab-import-file',
  template: `
<!-- Modal -->
    <div class="modal-dialog " role="document">
      <div class="modal-content">
        <div class="modal-body">
          <div class="p-grid">
            <div class="p-col-12">
              <div class="card">
                <div class="p-fluid p-formgrid p-grid">
                  <div class="form-group">
                  <p-fileUpload (uploadHandler)="submit($event)" cancelLabel="Clear" customUpload="true"
                         accept=".csv" maxFileSize="3000000">
                          <ng-template pTemplate="content">
                              <ul *ngIf="uploadedFiles.length">
                                  <li *ngFor="let file of uploadedFiles">{{file.name}} - {{file.size}} bytes</li>
                              </ul>
                          </ng-template>
                  </p-fileUpload>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [
  ]
})
export class TabImportFileComponent implements OnInit {

  @Input() source;

  @ViewChild('fileInput') fileInput: ElementRef;

  uploadedFiles: any[] = [];

	// I initialize the app component.
  constructor(private fb: FormBuilder, private cd: ChangeDetectorRef,
    private poService: POService,
    private messageService: MessageService
      ) {

	}

  ngOnInit(): void {
  }

  submit(event){

    for(let file of event.files) {
      this.uploadedFiles.push(file);
    }

    if (this.uploadedFiles && this.uploadedFiles.length > 0){

      let reader = new FileReader();
      reader.readAsDataURL(this.uploadedFiles[0]);
      reader.onload = () => {
        let body = new URLSearchParams();
        body.set('entityName', this.source);
        body.set('fileContent', (reader.result as string).split(',')[1]);


        this.poService.executeProcessByName('importDataFile',body.toString(), false).subscribe (
          (data: any) => {
            this.messageService.add({severity: 'success', summary: 'Successful', detail: 'Records successfully imported!', life: 3000});;
          },
          error => {
            this.messageService.add({severity: 'error', summary: 'Error', detail: error.message, life: 3000});

          }
        );
        this.clearFile();
      };
    }
  }

  clearFile() {
    this.uploadedFiles = [];
  }

}
