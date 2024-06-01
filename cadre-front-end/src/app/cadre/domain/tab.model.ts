import { FieldModel } from "./field.model";
import { POModel } from "./po.model";

export class TabModel extends POModel {
  static m_table_id: number = 8;
  static tableName: string = 'AD_Tab';

  /** Column name TabLevel */
  static readonly COLUMNNAME_TabLevel: string = "TabLevel";

  _id: number= Math.random();
  _fields : FieldModel [] = [];

  _fieldsLoaded: boolean = false;

  _fieldsGroup;

  constructor() {
    super();
    this.initColumnsName(
      [
        "AD_Window_ID",
        "SeqNo",
        "Help",
        "IsReadOnly",
        "AD_Tab_ID",
        "Name",
        "Description",
        "AD_Table_ID",
        "AD_Client_ID",
        "AD_Org_ID",
        "IsActive",
        "Created",
        "CreatedBy",
        "Updated",
        "UpdatedBy",
        TabModel.COLUMNNAME_TabLevel,
        "IsInsertRecord",
        "Parent_Column_ID",
        "Parent_ColumnName",
        "EntityType",
        "OrderByClause"
      ]);
  }

  get poTableName(){
      return TabModel.tableName;
  }

  get table_ID() {
      return TabModel.m_table_id;
  }

  get ad_client_id(): any {
    return this.getValueByColumnName("AD_Client_ID");
  }

  get ad_org_id(): any {
    return this.getValueByColumnName("AD_Org_ID");
  }

  get ad_tab_id(): any {
    return this.getValueByColumnName("AD_Tab_ID");
  }

  get ad_tab_uu(): any {
    return this.getValueByColumnName("AD_Tab_UU");
  }

  get ad_table_id(): any {
    return this.getValueByColumnName("AD_Table_ID");
  }

  get ad_window_id(): any {
    return this.getValueByColumnName("AD_Window_ID");
  }

  get created(): any {
    return this.getValueByColumnName("Created");
  }

  get description(): any {
    return this.getValueByColumnName("Description");
  }

  get entitytype(): any {
    return this.getValueByColumnName("EntityType");
  }

  get isactive(): any {
    return this.getValueByColumnName("IsActive");
  }

  get isInsertRecord(): any {
    return this.getValueByColumnName("IsInsertRecord");
  }

  get isReadOnly(): any {
    return this.getValueByColumnName("IsReadOnly");
  }

  get name(): any {
    return this.getValueByColumnName("Name");
  }

  get parent_column_id(): any {
    return this.getValueByColumnName("Parent_Column_ID");
  }

  get parent_column_name(): any {
    return this.getValueByColumnName("Parent_ColumnName");
  }

  get seqNo(): any {
    return this.getValueByColumnName("SeqNo");
  }

  get tabLevel(): any {
    return this.getValueByColumnName(TabModel.COLUMNNAME_TabLevel);
  }

  get updated(): any {
    return this.getValueByColumnName("Updated");
  }

  get updatedBy(): any {
    return this.getValueByColumnName("UpdatedBy");
  }

  get orderByClause(): any {
    return this.getValueByColumnName("OrderByClause");
  }


  /*constructor(id: number,name: string, fieldsGroup: FieldGroupModel []){
    this.id=id;
    this.name=name;
    this.fieldsGroup=fieldsGroup;
  }*/

  get fields(): FieldModel[]{
    return this._fields;
  }

  set fields(fields: FieldModel []){
    this._fieldsLoaded= true;
    this._fields = fields;
  }

  get fieldsGroup(): [][]{
    if (!this._fieldsGroup){
      let fieldGroupIndex:number = -1 ;
      let fieldsLength = this._fields.length;

      if (fieldsLength > 0){
        this._fieldsGroup = [];

              for(var i: number = 0; i < fieldsLength; i++) {
        
                let fieldModel =  this._fields[i];
        
                //if is the first record or is not in the same line
                //create new group
                if (i==0 || !fieldModel.issameline){
                  fieldGroupIndex++;
                  this._fieldsGroup[fieldGroupIndex]= [];
                }
        
                this._fieldsGroup[fieldGroupIndex].push(fieldModel);
        
              }

      }else{
        this._fieldsGroup = undefined;

      }

    }


    return this._fieldsGroup;
  }

}
