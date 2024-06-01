import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { FormGroup, FormControl } from "@angular/forms";
import { POModel } from "../domain/po.model";
import { POService } from "./po.service";
import { NgxIndexedDBService } from "ngx-indexed-db";
import { TabModel } from "../domain/tab.model";
import { FieldModel } from "../domain/field.model";
import { CriteriaBuilder } from "../filter/criteria-builder.model";
import { Restrictions } from "../filter/restrictions.factory";
import { ToolBarButton } from "../domain/toolbarbutton.model ";
import { ProcessParamModel } from "../domain/process-param.model";
import { TableModel } from "../domain/table.model";
import { Criteria } from "../filter/criteria.model";
import { Env } from "../util/env";
import { Criterion } from "../filter/criterion.model";
import { TableService } from "./table.service";
import { GenericPO } from "../domain/generic.po.model";
import { MessageService } from "primeng/api";
import { DisplayType } from "../domain/display-tpe.constant";


@Injectable()
export class GridTab {
  currentLevel: number;
  currentTabModel: TabModel;

  selectedModelChanged = new Subject<POModel>();
  tabFormGroup: FormGroup;

  /* record on the current table */
  selectedModel: POModel;

  /* The table that this tab manage */
  _table: TableModel;

  /* check if this tab has been loaded before*/
  tabLoaded: boolean = false;

  /* get models manage by this tab*/
  private models: POModel[];

  /* use to navigage between the models*/
  private currentIndex = 0;

  _serchMode: boolean = false;
  _searchEnabled: boolean = false;
  _newRecord: boolean = false;
  _currentFilter: Criteria;

  _numberOfRecords: number = 0;
  _numberOfRecordsPage: number = 10;
  _numberOfPages: number = 0;
  _currentPage: number = 1;

  _customToolBarButtons: ToolBarButton[] = [];

  constructor(
    private poService: POService,
    private dbService: NgxIndexedDBService,
    private messageService: MessageService,
    private tableService: TableService,
  ) {}

    /**
   * Loaded tab according to database
   */
     initTab(reload: boolean,currentLevel,currentTabModel) {
      this.currentLevel=currentLevel;
      this.currentTabModel=currentTabModel;

      Env.setContext(Env.CURRENT_WINDOW, this.currentTabModel.ad_window_id);

      Env.setContext(Env.CURRENT_TAB, this.currentLevel);

      if (this.getParentTabNo() >= 0) {
        Env.setContext(Env.PARENT_TAB, this.getParentTabNo());
      }

      if (reload) {
        this.resetPagination();
      }

      this.loadContext(this.currentTabModel, true);

      if (!this.tabLoaded) {
        this.loadFields(this.currentTabModel).then(() => {
          this.tabFormGroup = this.toFormGroup(this.currentTabModel.fields);
          this.loadToolbarButtons(this.currentTabModel)
            .then((resp: ToolBarButton[])=>{
              this._customToolBarButtons = resp;
          });
          this.loadTableInfo(reload)
        });
      } else {
        this.loadTableInfo(reload)
      }

      if (reload) {
        this._serchMode = false;
        this._searchEnabled = false;
      }
    }


  onClickButton(value) {
    if (value=='undo'){
      this.onClickUndoButton();
    }else if (value=='new'){
      this.onClickNewRecord();
    }else if (value=='save'){
      this.onClickSave();
    } else if (value=='duplicate'){
      this.onClickDuplicate();
    }  else if (value=='delete'){
      this.onClickDelete();
    }  else if (value=='enableSearch'){
      this.onClickEnableSearch();
    }  else if (value=='previous'){
      this.onClickLeft();
    }   else if (value=='next'){
      this.onClickRight();
    }    else if (value=='executeSearch'){
      this.onClickExecuteSearch();
    }    else if (value=='disableSearch'){
      this.onClickDisableSearch();
    }
  }

  enableButton(value): boolean {
    if (value=='undo'){
     return this.enableUndoButton();
    }else if (value=='new'){
      return !this._newRecord && (this.currentTabModel && this.currentTabModel.isInsertRecord);
    }else if (value=='save'){
      return this._newRecord || (this.currentTabModel && !this.currentTabModel.isReadOnly);
    }else if (value=='duplicate'){
      return !this._newRecord && this.currentTabModel && this.currentTabModel.isInsertRecord;
    }else if (value=='delete'){
      return !this._newRecord && this.currentTabModel && !this.currentTabModel.isReadOnly;
    } else if (value=='enableSearch'){
      return !this._newRecord;
    } else if (value=='previous'){
      return this.enableLeftButton();
    }  else if (value=='next'){
      return this.enableRightButton();
    }  else if (value=='customButton'){
      return !this._newRecord && this.currentTabModel&& !this.currentTabModel.isReadOnly
    } else if (value=='import'){
      return this.currentTabModel && this.currentTabModel.isInsertRecord;
    }
    else{
      return false;
    }

  }



  loadFields(currentTab: TabModel): Promise<void> {
    return new Promise((resolve) => {
      //Get fields
      if (currentTab && currentTab._fieldsLoaded) {
        resolve();
      } else {
        this.dbService
          .getAllByIndex(
            FieldModel.tableName,
            "AD_Tab_ID",
            IDBKeyRange.only(currentTab.getKeyValue())
          )
          .subscribe((fieldsData) => {
            let fields: FieldModel[] = [];

            if (fieldsData && fieldsData.length > 0) {
              let keys: any[] = Object.keys(fieldsData);

              keys.forEach((currentKey) => {
                let fieldJSON = fieldsData[currentKey];
                let fieldModel = new FieldModel();
                fieldModel.loadFromJSON(fieldJSON);
                fields.push(fieldModel);
              });

              if (fields) {
                fields.sort((a: FieldModel, b: FieldModel) => {
                  return a.seqno - b.seqno;
                });
              }
              currentTab.fields = fields;

              resolve();
            } else {
              let filter = new CriteriaBuilder()
                .from(FieldModel.tableName)
                .addCriterion(
                  Restrictions.eq("AD_Tab_ID", currentTab.getKeyValue())
                )
                .build();

              this.poService
                .getTableData(filter, true)
                .subscribe((resp: any) => {
                  let data = resp.value;
                  if (data) {
                    let keys: any[] = Object.keys(data);

                    keys.forEach((currentKey) => {
                      let fieldJSON = data[currentKey];
                      let fieldModel = new FieldModel();
                      fieldModel.loadFromJSON(fieldJSON);
                      fields.push(fieldModel);

                      this.dbService.add(FieldModel.tableName, {
                        AD_Field_ID: fieldJSON["AD_Field_ID"],
                        AD_Tab_ID: fieldJSON["AD_Tab_ID"],
                        IsSameLine: fieldJSON["IsSameLine"],
                        IsReadOnly: fieldJSON["IsReadOnly"],
                        Help: fieldJSON["Help"],
                        Description: fieldJSON["Description"],
                        AD_Column_ID: fieldJSON["AD_Column_ID"],
                        IsDisplayed: fieldJSON["IsDisplayed"],
                        AD_Reference_ID: fieldJSON["AD_Reference_ID"],
                        AD_Reference_Value_ID:
                          fieldJSON["AD_Reference_Value_ID"],
                        IsMandatory: fieldJSON["IsMandatory"],
                        DefaultValue: fieldJSON["DefaultValue"],
                        DynamicValidation: fieldJSON["DynamicValidation"],
                        SeqNo: fieldJSON["SeqNo"],
                        Label: fieldJSON["Label"],
                        BootstrapClass: fieldJSON["BootstrapClass"],
                        Placeholder: fieldJSON["Placeholder"],
                        IsDisplayedGrid: fieldJSON["IsDisplayedGrid"],
                        ColumnName: fieldJSON["ColumnName"],
                        AD_Process_ID: fieldJSON["AD_Process_ID"],

                        ProcessValue: fieldJSON["ProcessValue"],

                      }).subscribe((key) => {
                        console.log('key: ', key);
                      });
                    });

                    if (fields) {
                      fields.sort((a: FieldModel, b: FieldModel) => {
                        return a.seqno - b.seqno;
                      });
                      currentTab.fields = fields;
                    }
                  }

                  resolve();
                });
            }
          });
      }
    });
  }

  toFormGroup(fields: FieldModel[]) {
    let group: any = {};
    fields.forEach((element) => {
      group[element.columnName] = new FormControl("");
    });
    return new FormGroup(group);
  }

  loadToolbarButtons(currentTab: TabModel) {
    return new Promise<ToolBarButton[]>((resolve) => {
      let filter = new CriteriaBuilder()
        .from(ToolBarButton.tableName)
        .addCriterion(Restrictions.eq("AD_Tab_ID", currentTab.getKeyValue()))
        .build();

      let customToolbarButtons: ToolBarButton[] = [];
      this.poService.getTableData(filter, true).subscribe((resp: any) => {
        let data = resp.value;
        if (data) {
          let keys: any[] = Object.keys(data);

          keys.forEach((currentKey) => {
            let json = data[currentKey];
            let model = new ToolBarButton();
            model.loadFromJSON(json);
            customToolbarButtons.push(model);
          });

        }

        resolve(customToolbarButtons);
      });
    });
  }

  loadProcessParams(adProcessID: number) {
    return new Promise<ProcessParamModel[]>((resolve) => {
      let filter = new CriteriaBuilder()
        .from(ProcessParamModel.tableName)
        .addCriterion(Restrictions.eq("AD_Process_ID", adProcessID))
        .build();

      let processParams: ProcessParamModel[] = [];
      this.poService.getTableData(filter, true).subscribe((resp: any) => {
        let data = resp.value;
        if (data) {
          let keys: any[] = Object.keys(data);

          keys.forEach((currentKey) => {
            let json = data[currentKey];
            let model = new ProcessParamModel();
            model.loadFromJSON(json);
            processParams.push(model);
          });

        }

        resolve(processParams);
      });
    });
  }

  selectPage(pageNum: number) {
    this._currentPage = pageNum;
    this.currentIndex++;
    this.loadTabData(true, false);
  }


  /**
   * Action when previous(left) button is clicked
   */
   private selectPreviousRecord() {
    if (this.models && this.models.length > 0) {
      if (this.currentIndex > 0) {
        this.currentIndex--;
        this.setSelectedModel(this.models[this.currentIndex]);
      } else if (this.currentIndex === 0) {
        this.setSelectedModel(this.models[this.currentIndex]);
      }
    } else {
      this.onClickNewRecord();
    }
  }

  onClickExecuteSearch() {
    this._currentPage = 1;

    let criteriaBuilder = new CriteriaBuilder();
    criteriaBuilder.from(this._table.tableName);

    this._table.columns.forEach((column) => {
      let columnName = column.columnName;
      let columnValue = this.tabFormGroup.value[columnName];
      if (columnValue) {
        if (DisplayType.isString(column.ad_reference_id)) {
          criteriaBuilder.addCriterion(
            Restrictions.like(columnName, this.tabFormGroup.value[columnName])
          );
        } else {
          criteriaBuilder.addCriterion(
            Restrictions.eq(columnName, this.tabFormGroup.value[columnName])
          );
        }
      }
    });

    let parentCriterion = this.getParentRestriction();
    if (parentCriterion) {
      criteriaBuilder.addCriterion(parentCriterion);
    }

    criteriaBuilder.orderBy(this.currentTabModel.orderByClause);

    this._serchMode = false;
    this._searchEnabled = true;

    this._currentFilter = criteriaBuilder.build();
    this.loadTabData(false, true);
  }


  onClickUndoButton() {
    if (!this._newRecord) {
      this.selectedModel.reset();
      this.setSelectedModel(this.selectedModel);
    } else {
      if (this.models && this.models.length > 0) {
        this.setSelectedModel(this.models[this.currentIndex]);
      } else if (this._searchEnabled) {
        this.onClickDisableSearch();
      } else {
        this.onClickNewRecord();
      }
    }
  }

    /**
   * onClickDelete
   */
     onClickDelete() {
      let success: boolean = false;
      if (!this.selectedModel.is_New()) {
        this.poService.delete(this.selectedModel, false).subscribe(
          (data) => {
            this.models.splice(this.currentIndex, 1);
            this.messageService.add({severity: 'success', summary: 'Success', detail: 'Registro excluído com sucesso!', life: 3000});
            this.selectPreviousRecord();
          },
          (error) => {
            success = false;
          }
        );
      } else {
        this.messageService.add({severity: 'success', summary: 'Success', detail: 'Registro excluído com sucesso!', life: 3000});

      }
    }


  onClickDuplicate() {
    let newModel = this.selectedModel.duplicate();
    this.setSelectedModel(newModel);
    this._newRecord = true;
    this.messageService.add({severity: 'info', summary: 'Success', detail: 'Registro duplicado', life: 3000});

  }

  onClickEnableSearch() {
    this._serchMode = true;
    let newModel = new GenericPO(this._table);
    this.setSelectedModel(newModel);
  }

  onClickDisableSearch() {
    this._currentFilter = this.getDefaultFilter();
    this.resetPagination();
    this.loadTabData(false, true);
    this._serchMode = false;
    this._searchEnabled = false;
  }

    /**
   * onClickNewRecord
   */
     onClickNewRecord() {
      let newModel = new GenericPO(this._table);
      if (this.currentTabModel.tabLevel > 0) {
        let parentColumnName = this.currentTabModel.parent_column_name;
        if (parentColumnName) {
          let parentColumnValue = Env.getWindowContext(
            false,
            this.currentTabModel.ad_window_id,
            this.getParentTabNo(),
            parentColumnName
          );

          if (
            typeof parentColumnValue != "undefined" &&
            parentColumnValue != null
          ) {
            newModel.setValueByColumnName(parentColumnName, +parentColumnValue);
          }
        }
      }

      // this.models.push(newModel);

      this.setSelectedModel(newModel);
      if (this._newRecord) {
        this.messageService.add({severity: 'info', summary: 'Info', detail: 'Novo registro criado', life: 3000});
      }
    }


  /**
   * onClickLeft
   */
  onClickLeft() {
    this.selectPreviousRecord();
  }

  /**
   * onClickRight
   */
  onClickRight() {
    this.selectNextRecord();
  }


  public loadTableInfo(reload: boolean){
    //Get Table Info
  //  if (!this.models){
      this.tableService
      .getTableById(this.currentTabModel.ad_table_id)
      .then((tableModel) => {
        this._table = tableModel;

        if (!this.tabLoaded || reload) {
          this._currentFilter = this.getDefaultFilter();
        }

        if (!this.models || reload) {
          this.loadTabData(false, true);
        } else {
          this.setSelectedModel(this.models[this.currentIndex]);
        }

        this.tabLoaded = true;
      });

    //}
  }

  private getDefaultFilter(): Criteria {
    if (this._table) {
      let criteriaBuilder = new CriteriaBuilder();
      criteriaBuilder.from(this._table.tableName);
      criteriaBuilder.orderBy(this.currentTabModel.orderByClause);

      let parentCriterion = this.getParentRestriction();
      if (parentCriterion) {
        criteriaBuilder.addCriterion(parentCriterion);
      }

      return criteriaBuilder.build();
    }

    return null;
  }


  /**
   *
   * @param force
   */
   countRecords(force: boolean) {
    return new Promise<number>((resolve: (res: number) => void) => {
      if (!force && this._numberOfRecords) {
        resolve(this._numberOfRecords);
      } else {
        let filter = this._currentFilter;
        filter.count(true);

        this.poService
          .getTableData(filter, false)
          .toPromise()
          .then(
            (data: any) => {
              if (data) {
                this._numberOfRecords = +data["@odata.count"];
                resolve(this._numberOfRecords);
              }
            },
            (error) => {
              this._numberOfRecords = 0;
              resolve(this._numberOfRecords);
            }
          );
      }
    });
  }


  /**
   * Load data to show in the tab
   */
   loadTabData(append: boolean, force: boolean) {
    this.countRecords(force).then((data: number) => {
      if (data) {
        this._numberOfRecords = data;
        this._numberOfPages = Math.ceil(
          this._numberOfRecords / this._numberOfRecordsPage
        );
        let filter = this._currentFilter;
        if (filter) {
          filter.top(this._numberOfRecordsPage);
          filter.skip(this._numberOfRecordsPage * (this._currentPage - 1));
          filter.count(false);
          if (!append) {
            this.models = [];
          }
          this.poService.getModels(this._table, filter, false).subscribe(
            (models: []) => {
              this.models =
                append && this.models ? this.models.concat(models) : models;
              if (this.models && this.models.length > 0) {
                if (this.currentIndex >= this.models.length) {
                  this.currentIndex = 0;
                }
                this._newRecord = false;
                this.setSelectedModel(this.models[this.currentIndex]);
              } else {
                if (!this._searchEnabled) {
                  this.onClickNewRecord();
                }
              }
            },
            (error) => {
              this.models = [];
              //this.onClickNewRecord();
            }
          );
        }
      } else {
        if (!this._searchEnabled) {
          this.onClickNewRecord();
        }
      }
    });

    //Env.setContext...
  }

  /**
   * onClickSave
   */
  onClickSave() {

    if (!this.tabFormGroup.valid){
      Object.keys(this.tabFormGroup.controls).forEach(key => {
        if(!this.tabFormGroup.get(key).valid){
          this.tabFormGroup.get(key).markAsTouched();
          this.tabFormGroup.get(key).markAsDirty();
        }
      });

      this.messageService.add({severity:'warn', summary:'Warning', detail:'Informar campos obrigatórios', life: 3000});

    }else{
      let columnNames: any[] = Object.keys(this.tabFormGroup.value);
      columnNames.forEach((column) => {
        this.selectedModel.setValueByColumnName(
          column,
          this.tabFormGroup.value[column]
        );
      });

      let newRecord = this.selectedModel.is_New();
      if (newRecord || this.selectedModel.is_Changed()) {
        this.poService
          .savePO(this.selectedModel, false)
          .subscribe((data: any) => {
            this.selectedModel.loadFromJSON(data); // success path
            if (newRecord) {
              if (this.models) {
                this.models.splice(this.currentIndex, 0, this.selectedModel);
              }
              this.messageService.add({severity: 'success', summary: 'Successful', detail: 'Registro criado com sucesso!', life: 3000});;
            } else {
              this.messageService.add({severity: 'success', summary: 'Successful', detail: 'Registro alterado com sucesso!', life: 3000});

            }
            this._newRecord = false;
            this.setSelectedModel(this.selectedModel);
          });
      } else {
        this.messageService.add({severity:'warn', summary:'Warning', detail:'Registro não foi alterado', life: 3000});
      }

    }
  }

    /**
   * get parent tab no
   */
    public getParentTabNo(): number {
      let tabNo = this.currentLevel;
      let currentLevel = this.currentTabModel.tabLevel;
      let parentNo = currentLevel - 1;

      if (parentNo < 0) {
        return tabNo;
      }
      do {
        tabNo--;

        let upTabLevel = Env.getWindowContext(
          true,
          this.currentTabModel.ad_window_id,
          tabNo,
          TabModel.COLUMNNAME_TabLevel
        );

        if (typeof upTabLevel !== "undefined") {
          currentLevel = upTabLevel;
          if (currentLevel == 0) {
            break;
          }
        } else {
          tabNo--;
          break;
        }
      } while (parentNo != currentLevel);

      return tabNo;
    }



  private getParentRestriction(): Criterion {
    let tabLevel = this.currentTabModel.tabLevel;
    if (tabLevel > 0) {
      let parentColumnName = this.currentTabModel.parent_column_name;
      if (parentColumnName) {
        let parentColumnValue = Env.getWindowContext(
          false,
          this.currentTabModel.ad_window_id,
          this.getParentTabNo(),
          parentColumnName
        );

        if (parentColumnValue) {
          return Restrictions.eq(parentColumnName, +parentColumnValue);
        }
      }
    }

    return null;
  }

    /**
   * Action when next(right) button is clicked
   */
     private selectNextRecord() {
      this._newRecord = false;

      if (this.models && this.models.length > 0) {
        if (this.currentIndex < this.models.length - 1) {
          this.currentIndex++;
          this.setSelectedModel(this.models[this.currentIndex]);
        } else if (this._currentPage !== this._numberOfPages) {
          this.selectPage(this._currentPage + 1);
        }
      } else {
        this.setSelectedModel(null);
      }
    }

  /**
   * Manage selected model
   * @param model
   */
   private setSelectedModel(model: POModel) {
    this.selectedModel = model;
    this._newRecord = model.is_New();
    this.loadContext(model, false);

    this.selectedModelChanged.next(this.selectedModel);
  }


  /**
   * Check if this tab should enable left button
   */
   enableLeftButton(): boolean {
    if (this.models && !this._newRecord) {
      return this.currentIndex > 0;
    } else {
      return false;
    }
  }


  /**
   * Check if this tab should enable right button
   */

   enableRightButton(): boolean {
    if (this.models && !this._newRecord) {
      return (
        this.currentIndex < this.models.length - 1 ||
        this._currentPage !== this._numberOfPages
      );
    } else {
      return false;
    }
  }

  enableUndoButton(): boolean {
    return true;
  }

  public loadContext(model: POModel, metadata: boolean) {
    let windowNo: number = this.currentTabModel.ad_window_id;
    let tabNo: number = this.currentLevel;
    model._m_columns.forEach((columnName) => {
      Env.setWindowContext(
        metadata,
        windowNo,
        tabNo,
        columnName,
        model.getValueByColumnName(columnName)
      );
    });
  }

  public resetPagination() {
    this._currentPage = 1;
    this._numberOfPages = 0;
    this._numberOfRecords = 0;
    this.currentIndex = 0;
  }
}
