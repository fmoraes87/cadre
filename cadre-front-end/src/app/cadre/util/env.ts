export class Env{

    public static readonly AD_USER_ID: string = "#AD_User_ID";
	public static readonly AD_ORG_ID: string= "#AD_Org_ID";
    public static readonly AD_CLIENT_ID: string= "#AD_Client_ID";
    public static readonly AD_MENU_ID: string= "#AD_MENU_ID";
    public static readonly AD_LANUAGE_ID: string= "#AD_Language_ID";

    public static readonly CURRENT_WINDOW = '$AD_Window_ID';
    public static readonly CURRENT_TAB = '$AD_Tab_ID';
    public static readonly PARENT_TAB = '$AD_Parent_Tab_ID';


    static parseContext (context: string)
	{
        let result = sessionStorage.getItem(context)
        return result? JSON.parse(result):context;
    }

    static setContext (context: string , value: any)
	{
		sessionStorage.setItem(context,JSON.stringify(value) );
    }


    static setWindowContext (metadata: boolean , windowNo: number, tabNo: number, context: string , value: any)
	{
        let finalContext = metadata? "M|"+windowNo : ""+windowNo;
        finalContext = (tabNo==null || typeof tabNo === "undefined"? finalContext: finalContext+"|"+tabNo);
        finalContext =  finalContext+"|"+context;

        if(typeof value !== "undefined")
          sessionStorage.setItem(finalContext,JSON.stringify(value) );
        else
          sessionStorage.setItem(finalContext,undefined );

    }

    static getWindowContext (metadata: boolean, windowNo: number, tabNo: number, context: string): any
	{
        let finalContext = metadata? "M|"+windowNo : ""+windowNo;
        finalContext = (tabNo==null || typeof tabNo === "undefined"? finalContext: finalContext+"|"+tabNo);
        finalContext =  finalContext+"|"+context;

        let result = sessionStorage.getItem(finalContext)
        if (result!=="undefined")
          return result? JSON.parse(result):result;
        else
          return undefined;
    }

    static setGlobalContext (context: string , value: any)
	{
		localStorage.setItem(context,JSON.stringify(value) );
    }

    static getGlobalContext (context: string): any
	{
        let result = localStorage.getItem(context)
        return result? JSON.parse(result):result;
    }

    static removeGlobalItem (context: string): any
	{
		localStorage.removeItem(context);
    }

    static clearContex(metadata: boolean, windowNo: number, tabNo: number,  context: string){
        let finalContext = metadata? "M|"+windowNo : ""+windowNo;
        finalContext = (typeof tabNo === "undefined"? finalContext: finalContext+"|"+tabNo);
        finalContext =  finalContext+"|"+context;

		sessionStorage.removeItem(finalContext);
    }

    static clearAll(){
        sessionStorage.clear();
    }


}
