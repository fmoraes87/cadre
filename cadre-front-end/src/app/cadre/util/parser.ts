import { Env } from './env';

export class Parser{
    
/*    static parseCriteria(expression: string): Criteria {
        if (expression.toLowerCase().startsWith("@ref=")){
            let pos_opsqb = expression.indexOf("[");
            let pos_clsqb = expression.indexOf("]");
            if (pos_opsqb < 0 || pos_clsqb < 0) {
                // wrong format - it must be @Ref=Table[Where].Column
                return null;
            }
            let tableName = expression.substring(5, pos_opsqb);

            //Ex : AD_Table_ID eq @AD_Parent_Tab_ID@|@AD_Table_ID@]
            let where = this.parseFullExpression(expression.substring(pos_opsqb+1, pos_clsqb));
            if (where){
                let columnName: string; 

                if (expression.indexOf(".",pos_clsqb)> 0){
                    columnName = expression.substring(pos_clsqb+2);
                }

                let filter = new CriteriaBuilder()
                    .from(tableName)
                    .addCriterion(new CustomExpression(where))
                    .build();

                return filter;
            }else{
                return null;
            }
        }

        return null;

    }*/

    /**
     * ex: @$AD_Parent_Tab_ID@
     * @param variable 
     */
    static parseVariable(variable: string): any {
        if (variable.startsWith("#")){
            return Env.getGlobalContext(variable);
        }else if (variable.startsWith("$")){ 
            return  Env.parseContext(variable);
        } else{
            let windowNo = Env.parseContext(Env.CURRENT_WINDOW);
            let tabNo = Env.parseContext(Env.CURRENT_TAB);

            return Env.getWindowContext(false,windowNo,tabNo,variable);
        }


    }


    /**
     * Ex: AD_Column[AD_Table_ID eq <@$AD_Parent_Tab_ID@|AD_Table_ID>]
     * @param expression 
     */
    static parseFullExpression(fullExpression: string): any {
        if (fullExpression){
            let i = fullExpression.indexOf('<');
            let inStr: string = fullExpression;
            let outStr: string= '';
            while (i != -1){
                outStr+=inStr.substring(0, i); // up to @
                inStr = inStr.substring(i+1, inStr.length);	// from first @
                let j = inStr.indexOf('>');		 // second tag
                if (j < 0)
                {
                    return fullExpression; //	no second tag
                }
                
                let expression = this.parseExpression(inStr.substring(0, j));
                let value = Env.parseContext(expression);
    
                if (value==null){
                    return null;
                }
    
                outStr+=value;
    
                if (j+1 < inStr.length){
                    inStr = inStr.substring(j+1, inStr.length);	// from second @
                    i = inStr.indexOf('<');
                }else{
                    inStr='';
                    i = -1;
                    break;
                }	
            }
    
            outStr+=inStr;
            return outStr;
        }else{
            return null;
        }

    }

    /**
     * Ex: @$AD_Parent_Tab_ID@|AD_Table_ID
     * @param expression 4|AD_Table_ID
     */
    static parseExpression(expression: string): any {
        let i = expression.indexOf('@');
        let inStr: string = expression;
        let outStr: string= '';
        while (i != -1){
            outStr+=inStr.substring(0, i); // up to @
            inStr = inStr.substring(i+1, inStr.length);	// from first @
            let j = inStr.indexOf('@');		 // second tag
            if (j < 0)
			{
				return expression; //	no second tag
            }
            
            //@$AD_Parent_Tab_ID@
            let token = inStr.substring(0, j);

            let value = this.parseVariable(token);
            outStr+=value;

            if (value==null){
                return null;
            }

            if (j+1 < inStr.length){
                inStr = inStr.substring(j+1, inStr.length);	// from second @
                i = inStr.indexOf('@');
            }else{
                inStr='';
                i = -1;
                break;
            }	
        }

        outStr+=inStr;
        return outStr;
    }
 
 

}