import { DisplayType } from "../../domain/display-tpe.constant";
import { UIAmountEditorComponent } from "./ui-amount-editor.component";
import { UIDateEditorComponent } from "./ui-date-editor.component";
import { UIDateTimeEditorComponent } from "./ui-date-time-editor.component";
import { UIIntegerEditorComponent } from "./ui-integer-editor.component";
import { UIListComponent } from "./ui-list.component";
import { UIPasswordEditorComponent } from "./ui-password-editor.component";
import { UIStringEditorComponent } from "./ui-string-editor.component";
import { UITableDirectEditorComponent } from "./ui-table-direct-editor.component";
import { UITableEditorComponent } from "./ui-table-editor.component";
import { UITextEditorComponent } from "./ui-text-editor.component";
import { UIYesNoEditorComponent } from "./ui-yes-no-editor.component";
import { UIButton } from "./ui-button.component";

export class DefaultEditorFactory {

    static getElement(ad_reference_id: number) {
        if (ad_reference_id===DisplayType.Text){ //text
            return UITextEditorComponent;
        }else if ( ad_reference_id===DisplayType.String ) //String
        {
            return UIStringEditorComponent;
        }else if ( ad_reference_id===DisplayType.MapValues ) //String
        {
            return UIStringEditorComponent;
        }else if ( ad_reference_id===DisplayType.Collection ) //String
        {
            return UIStringEditorComponent;
        }else if (ad_reference_id===DisplayType.TableDir ) //TableDir
        {
            return UITableDirectEditorComponent;
        }else if (ad_reference_id===DisplayType.Table) //TableList
        {
            return UITableEditorComponent;
        }else if (ad_reference_id===DisplayType.List) //List
        {
            return UIListComponent;
        }else if (ad_reference_id===DisplayType.YesNo){ //Yes-No
            return UIYesNoEditorComponent;
        }else if (ad_reference_id===DisplayType.Integer){ //Integer
            return UIIntegerEditorComponent;
        }else if (ad_reference_id===DisplayType.Date){ //Date
            return UIDateEditorComponent;
        }else if (ad_reference_id===DisplayType.DateTime){ //DateTime
          return UIDateTimeEditorComponent;
        }else if (ad_reference_id===DisplayType.Password){ //Password
            return UIPasswordEditorComponent;
        }else if (ad_reference_id===DisplayType.Amount){ //Amount
            return UIAmountEditorComponent;
        }/*else if (ad_reference_id===DisplayType.File){ //File
          return UIFileComponent;
        }*/else if (!ad_reference_id){
            return UIButton;
        }else{
          return null;
        }
    }

}
