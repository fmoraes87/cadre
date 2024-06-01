export abstract class POModel {

 	/** Column name AD_Client_ID */
  static readonly COLUMNNAME_AD_Client_ID: string = "AD_Client_ID";
	/** Column name AD_Org_ID */
  static readonly COLUMNNAME_AD_Org_ID: string = "AD_Org_ID";
	/** Column name Created */
  static readonly COLUMNNAME_Created: string = "Created";
	/** Column name CreatedBy */
  static readonly COLUMNNAME_CreatedBy: string = "CreatedBy";
	/** Column name IsActive */
	static readonly COLUMNNAME_IsActive: string = "IsActive";
	/** Column name Updated */
	static readonly COLUMNNAME_Updated: string = "Updated";
	/** Column name UpdatedBy */
  static readonly COLUMNNAME_UpdatedBy: string = "UpdatedBy";



    /** Original Values         */
    private _m_oldValues: any[] = [];
    /** New Values              */
    private _m_newValues: any[] = [];
    /** Columns */
    _m_columns: string [] = [];

    abstract get poTableName();
    abstract get table_ID();

    protected initColumnsName(m_columns: string []){
      m_columns.forEach(column =>
        this._m_columns.push(column)
      );
    }

    loadFromJSON (jsonObj){
      let jsonKeyColumns = Object.keys(jsonObj);
      this._m_oldValues = []
      jsonKeyColumns.forEach(jsonColumnName => {
        let index = this.get_ColumnIndex(jsonColumnName);
        if (index >= 0){
          this._m_oldValues[index]=  jsonObj[jsonColumnName];
        }
      });
      this._m_newValues = [];
    }

    getValueById(index: number): any {
      if (index < 0 || index >= this._m_columns.length){
        //log.log(Level.WARNING, "Index invalid - " + index);
        return null;
      }
      if (typeof(this._m_newValues[index]) !== 'undefined'){
        return this._m_newValues[index];
      }else if (typeof(this._m_oldValues[index])  !== 'undefined'){
        return this._m_oldValues[index];
      }else{
        return undefined;
      }

    }

    getValueByColumnName(columnName: string) : any {
      let index = this.get_ColumnIndex(columnName);
      if (index < 0 ) {
        //log.log(Level.WARNING, "Column not found - " + columnName);
        return null;
      }

      return this.getValueById(index);
    }

    get_ColumnIndex(columnName: string): number{
      if (columnName){
        return this._m_columns.indexOf(columnName);
      }else{
        return -1;
      }
    }


    setValueByColumnName(columnName: string, value: any): boolean{
      let index = this.get_ColumnIndex(columnName);
      if (index < 0 ) {
        return false;
      }

      return this.setValueById(index,value);

    }

    setValueById(index: number, value: any): boolean{

      if (index < 0 || index >= this._m_columns.length)
      {
        return false;
      }
      let oldValue = this._m_oldValues[index];
      if (!oldValue || oldValue !=value){
        this._m_newValues[index] = value;
      }

      return true;
    }

    //TODO - user IsKey property to identify key column
    getKeyColumnName(): string{
      let tableName = this.poTableName;
      let keyColumnName = tableName + "_ID";

      return keyColumnName;
    }

    getKeyValue(): any{
      let keyColumnName = this.getKeyColumnName();
      let keyColumnValue = this.getValueByColumnName(keyColumnName);

      return keyColumnValue;
    }

    setKeyValue(value: any){
      this.setValueByColumnName(this.getKeyColumnName(),value);
    }

    getJSON(){
      var jsonVariable = {};

      this._m_columns.forEach( columnName => {

        if (!this.isColumnCalculatedAutomatically(columnName)){
          let value  = this.getValueByColumnName(columnName);
          if (!value && typeof(value)==='string'){
            value = null;
          }
          jsonVariable[columnName] = value;

        }

      });

      return jsonVariable;
    }

    getNewValuesASJSON(){
      var jsonVariable = {};

      this._m_columns.forEach( columnName => {

        if (!this.isColumnCalculatedAutomatically(columnName)){
          let index = this.get_ColumnIndex(columnName);
          if (this.is_ValueChanged(index)){
            let value  = this.getValueByColumnName(columnName);
            if (!value && typeof(value)==='string'){
              value = null;
            }else if (value && Object.prototype.toString.call(value) === "[object Date]" && !isNaN(value)){
              let currentTimeZone = new Date().getTimezoneOffset();
              value.setTime(value.getTime() + (currentTimeZone)*60*1000);

               let isoString = value.toISOString()
               value=isoString.substring(0, (isoString.indexOf("T")|0) + 6|0);
            }
            jsonVariable[columnName] = value;
          }

        }


      });

      return jsonVariable;
    }

    is_New(): boolean{
      let tableColumnIdValue = this.getKeyValue();
      return tableColumnIdValue==null || tableColumnIdValue === undefined;
    }

    is_Changed(): boolean{
      let isChanged:boolean  = false;
      this._m_columns.forEach( (columnName, i) => {
          isChanged = isChanged || this.is_ValueChanged(i);
      });

      return isChanged;
    }

    is_ValueChanged(index: number): boolean {
      if (index < 0 || index >= this._m_columns.length)
      {
        return false;
      }

      if (typeof (this._m_newValues[index])==='undefined' ){
        return false;
      }

      if (typeof (this._m_newValues[index])==='undefined'
            && typeof (this._m_oldValues[index])!=='undefined'){
        return false;
      }

      return this._m_newValues[index]!==(this._m_oldValues[index]);
    }

      	/**
  	 * 	Is Standard Column
  	 *	@return true for AD_Client_ID, etc.
  	 */
  	isColumnCalculatedAutomatically(columnName : String )
  	{
  		if (columnName === (POModel.COLUMNNAME_Created)
  			|| columnName === (POModel.COLUMNNAME_CreatedBy)
  			|| columnName ===(POModel.COLUMNNAME_Updated)
  			|| columnName === (POModel.COLUMNNAME_UpdatedBy) )
  			return true;

  		return false;
  	}	//	isStandardColumn


    duplicate(): POModel {
      let clone = deepCopy(this);

      clone._m_oldValues[this.get_ColumnIndex(this.getKeyColumnName())]=null;
      clone._m_newValues = [];
      return clone;
    }

    reset() {
      this._m_newValues = [];
    }

}

function deepCopy(obj) {
  let copy;

  // Handle the 3 simple types, and null or undefined
  if (null == obj || "object" != typeof obj) return obj;

  // Handle Array
  if (obj instanceof Array) {
      copy = [];
      for (var i = 0, len = obj.length; i < len; i++) {
          copy[i] = deepCopy(obj[i]);
      }
      return copy;
  }

  // Handle Object
  if (obj instanceof Object) {
      copy = Object.create(obj);
      for (var attr in obj) {
          if (obj.hasOwnProperty(attr)) copy[attr] = deepCopy(obj[attr]);
      }
      return copy;
  }

  throw new Error("Unable to copy obj! Its type isn't supported.");
}
