import { POModel } from "./po.model";

export class ToolBarButton extends POModel {
  static m_table_id: number = 55;
  static tableName: string = 'AD_ToolBarButton';

  _id: number= Math.random();

  constructor() {
    super();
    this.initColumnsName(
      [
        "AD_Process_ID",
        "AD_Client_ID",
        "AD_Org_ID",
        "AD_ToolBarButton_ID",
        "Created",
        "CreatedBy",
        "Icon",
        "IsActive",
        "Name",
        "Updated",
        "UpdatedBy",
        "Help",
        "ActionName",
        "AD_Tab_ID",
        "AD_ToolbarButton_UU",
        "IsLinkedToSelectedRecord",
        "ProcessValue",

    ]);
  }

  get poTableName(){
      return ToolBarButton.tableName;
  }

  get table_ID() {
      return ToolBarButton.m_table_id;
  }

  get ad_toolbarbutton_id(): any {
    return this.getValueByColumnName("AD_ToolBarButton_ID");
  }

  get ad_process_id(): any {
    return this.getValueByColumnName("AD_Process_ID");
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

  get icon(): any {
    return this.getValueByColumnName("Icon");
  }

  get description(): any {
    return this.getValueByColumnName("Description");
  }

  get ad_tab_id(): any {
    return this.getValueByColumnName("AD_Tab_ID");
  }

  get name(): any {
    return this.getValueByColumnName("Name");
  }

  get action_name(){
    return this.getValueByColumnName("ActionName");
  }

  get is_linked_selected_record(){
    return this.getValueByColumnName("IsLinkedToSelectedRecord");
  }

  get help(){
    return this.getValueByColumnName("Help");
  }

  get process_value(){
    return this.getValueByColumnName("ProcessValue");
  }


}
