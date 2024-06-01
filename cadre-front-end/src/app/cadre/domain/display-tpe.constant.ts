export class DisplayType {

	static readonly m_table_id: number = 102;


	/** Display Type 10	String	*/
	static readonly String: number = 10;
	/** Display Type 11	Integer	*/
	static readonly Integer: number = 11;
	/** Display Type 12	Amount	*/
	static readonly Amount: number = 12;
	/** Display Type 13	ID	*/
	static readonly ID: number = 13;
	/** Display Type 14	Text	*/
	static readonly Text: number = 14;
	/** Display Type 15	Date	*/
	static readonly Date: number = 15;
	/** Display Type 16	DateTime	*/
	static readonly DateTime: number = 16;
	/** Display Type 17	List	*/
	static readonly List: number = 17;
	/** Display Type 18	Table	*/
	static readonly Table: number = 18;
	/** Display Type 19	TableDir	*/
	static readonly TableDir: number = 19;
	/** Display Type 20	YN	*/
	static readonly YesNo: number = 20;
	/** Display Type 22	Number	*/
	static readonly Number: number = 22;
	/** Display Type 23	BLOB	*/
	static readonly Binary: number = 23;
	/** Display Type 24	Time	*/
	static readonly Time: number = 24;
	/** Display Type 36	CLOB: number	*/
	static readonly MapValues = 36;
	/** Display Type 36	CLOB: number	*/
	static readonly Collection = 28;
	/** Display Type 36	CLOB: number	*/
	static readonly Password = 42;
	/** Display Type 36	CLOB: number	*/
  static readonly File = 51;

	static isID ( displayType: number)
	{
		if (displayType == DisplayType.ID || displayType == DisplayType.Table || displayType == DisplayType.TableDir) {
			return true;
		}


		return false;
	}	//	isID


	static isNumeric(displayType: number)
	{
		if (displayType == DisplayType.Amount || displayType == DisplayType.Number
			|| displayType == DisplayType.Integer ) {
			return true;
		}


		return false;
	}	//	isNumeric

	static isDate (displayType: number){
		if (displayType == DisplayType.Date || displayType == DisplayType.DateTime || displayType == DisplayType.Time) {
			return true;
		}

		return false;
	}	//	isDate

	static isString (displayType: number){
		if (displayType == DisplayType.String || displayType == DisplayType.Text) {
			return true;
		}

		return false;
	}
}
