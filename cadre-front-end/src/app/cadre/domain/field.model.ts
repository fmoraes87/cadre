import { POModel } from "./po.model";

export class FieldModel extends POModel {
  static m_table_id: number = 9;
  static tableName: string = 'AD_Field';

  _id: number= Math.random();

  constructor() {
    super();
    this.initColumnsName(
      [
        "AD_Tab_ID",
        "IsSameLine",
        "AD_Field_ID",
        "IsReadOnly",
        "Help",
        "Description",
        "AD_Column_ID",
        "IsDisplayed",
        "AD_Client_ID",
        "AD_Org_ID",
        "IsActive",
        "Created",
        "CreatedBy",
        "Updated",
        "UpdatedBy",
        "AD_Reference_ID",
        "AD_Reference_Value_ID",
        "IsMandatory",
        "DefaultValue",
        "DynamicValidation",
        "EntityType",
        "SeqNo",
        "Label",
        "BootstrapClass",
        "Placeholder",
        "IsDisplayedGrid",
        "ColumnName",
        "AD_Process_ID",
        "ProcessValue"
    ]);
  }

  get poTableName(){
      return FieldModel.tableName;
  }

  get table_ID() {
      return FieldModel.m_table_id;
  }

  get ad_field_id(): any {
    return this.getValueByColumnName("AD_Field_ID");
  }

  get ad_client_id(): any {
    return this.getValueByColumnName("AD_Client_ID");
  }

  get ad_org_id(): any {
    return this.getValueByColumnName("AD_Org_ID");
  }

  get isactive(): any {
    return this.getValueByColumnName("IsActive");
  }

  get created(): any {
    return this.getValueByColumnName("Created");
  }

  get createdby(): any {
    return this.getValueByColumnName("CreatedBy");
  }

  get updated(): any {
    return this.getValueByColumnName("Updated");
  }

  get updatedby(): any {
    return this.getValueByColumnName("UpdatedBy");
  }

  get label(): any {
    return this.getValueByColumnName("Label");
  }

  get placeholder(): any {
    let placeholder  = this.getValueByColumnName("Placeholder");
    if (placeholder || placeholder===0){
      return placeholder;
    }else{
      return "";
    }
  }

  get description(): any {
    return this.getValueByColumnName("Description");
  }

  get ad_tab_id(): any {
    return this.getValueByColumnName("AD_Tab_ID");
  }


  get ad_process_id(): any {
    return this.getValueByColumnName("AD_Process_ID");
  }

  get isdisplayed(): any {
    return this.getValueByColumnName("IsDisplayed");
  }

  get isreadonly(){
    return this.getValueByColumnName("IsReadOnly");
  }

  get issameline(){
    return this.getValueByColumnName("IsSameLine");
  }

  get seqno(){
    return this.getValueByColumnName("SeqNo");
  }

  get defaultvalue(): any {
    let defautlValue  = this.getValueByColumnName("DefaultValue");
    if (this.ad_reference_id===20){
      let bolValue = (defautlValue === 'Y' || defautlValue === 'true');
      return bolValue;
    }else{
     return defautlValue;
    }

  }

  set defaultvalue(value) {
    if (this.ad_reference_id===20){
      let bolValue = (value === 'Y' || value === 'true');
      this.setValueByColumnName("DefaultValue",bolValue);
    }else{
      this.setValueByColumnName("DefaultValue",value);
    }

  }

  get ismandatory(): any {
    return this.getValueByColumnName("IsMandatory");
  }

  get boostrapClass(): any {
    return this.getValueByColumnName("BootstrapClass");
  }

  get ad_column_id(): any {
    return this.getValueByColumnName("AD_Column_ID");
  }

  get columnName(): any {
    return this.getValueByColumnName("ColumnName");
  }

  get isdisplayedgrid(): any {
    return this.getValueByColumnName("IsDisplayedGrid");
  }

  get ad_reference_id(): any {
    return this.getValueByColumnName("AD_Reference_ID");
  }

  get ad_reference_value_id(): any {
    return this.getValueByColumnName("AD_Reference_Value_ID");
  }

  get process_value(): any {
    return this.getValueByColumnName("ProcessValue");
  }


  get dynamic_validation(): any {
    return this.getValueByColumnName("DynamicValidation");
  }

}
