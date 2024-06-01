export class ValueNamePair {

    static EMPTY: ValueNamePair = new ValueNamePair("", "");

    /** The Name        */
    private  m_name: String;
    
    /** The Value       */
    private  m_value: String;
    
    constructor(value: String , name: String ){

        this.m_name = name;
		if (typeof this.m_name === 'undefined'){
            this.m_name = "";
        }

        this.m_value = value;
		if (typeof this.m_value === 'undefined'){
            this.m_value = "";
        }

    }

    get name()
	{
		return this.m_name;
    }
    
    get value(){
        return this.m_value;
    }
}