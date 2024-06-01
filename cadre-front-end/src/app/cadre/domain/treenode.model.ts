import { POModel } from "./po.model";

export class TreeNodeModel extends POModel {
    static readonly m_table_id: number = 15;
    static readonly tableName: string = 'AD_TreeNode';

    constructor() {
        super();
        this.initColumnsName(
            [
                "AD_Client_ID",
                "AD_Org_ID",
                "AD_Tree_ID",
                "AD_Window_ID",
                "AD_TreeNode_ID",
                "AD_TreeNode_Parent_ID",
                "Name",
                "Created",
                "CreatedBy",
                "IsActive",
                "IsSummary",
                "SeqNo",
                "Updated",
                "UpdatedBy"
            ]);
    }

    get poTableName() {
        return TreeNodeModel.tableName;
    }

    get table_ID() {
        return TreeNodeModel.m_table_id;
    }

    get ad_client_id(): any {
        return this.getValueByColumnName("AD_Client_ID");
    }

    set ad_client_id(value) {
        this.setValueByColumnName("AD_Client_ID", value);
    }

    get ad_org_id(): any {
        return this.getValueByColumnName("AD_Org_ID");
    }

    set ad_org_id(value) {
        this.setValueByColumnName("AD_Org_ID", value);
    }

    get name(): any {
        return this.getValueByColumnName("Name");
    }

    set name(value) {
        this.setValueByColumnName("Name", value);
    }


    get ad_tree_id(): any {
        return this.getValueByColumnName("AD_Tree_ID");
    }

    set ad_tree_id(value) {
        this.setValueByColumnName("AD_Tree_ID", value);
    }

    get ad_treenode_parent_id(): any {
        return this.getValueByColumnName("AD_TreeNode_Parent_ID");
    }

    set ad_treenode_parent_id(value) {
        this.setValueByColumnName("AD_TreeNode_Parent_ID", value);
    }

    get ad_treenode_id(): any {
        return this.getValueByColumnName("AD_TreeNode_ID");
    }

    set ad_treenode_id(value) {
        this.setValueByColumnName("AD_TreeNode_ID", value);
    }

    get ad_window_id(): any {
        return this.getValueByColumnName("AD_Window_ID");
    }

    set ad_window_id(value) {
        this.setValueByColumnName("AD_Window_ID", value);
    }

    get created(): any {
        return this.getValueByColumnName("Created");
    }

    set created(value) {
        this.setValueByColumnName("Created", value);
    }

    get createdby(): any {
        return this.getValueByColumnName("CreatedBy");
    }

    set createdby(value) {
        this.setValueByColumnName("CreatedBy", value);
    }

    get isactive(): any {
        return this.getValueByColumnName("IsActive");
    }

    set isactive(value) {
        this.setValueByColumnName("IsActive", value);
    }

    get issummary(): any {
        return this.getValueByColumnName("IsSummary");
    }

    set issummary(value) {
        this.setValueByColumnName("IsSummary", value);
    }

    get seqno(): any {
        return this.getValueByColumnName("SeqNo");
    }

    set seqno(value) {
        this.setValueByColumnName("SeqNo", value);
    }

    get updated(): any {
        return this.getValueByColumnName("Updated");
    }

    set updated(value) {
        this.setValueByColumnName("Updated", value);
    }

    get updatedby(): any {
        return this.getValueByColumnName("UpdatedBy");
    }

    set updatedby(value) {
        this.setValueByColumnName("UpdatedBy", value);
    }

}