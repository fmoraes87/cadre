export class CEvent  {


    _topic: string;
    _m_values: any[];
    _m_properties: string [];

    getValueById(index: number): any {
      if (index < 0 || index >= this._m_properties.length){
        //log.log(Level.WARNING, "Index invalid - " + index);
        return null;
      }
      if (typeof(this._m_values[index]) !== 'undefined'){
        return this._m_values[index];
      }else{
        return undefined;
      }

    }

    getValueByPropertieName(propertie: string) : any {
      let index = this.get_ColumnIndex(propertie);
      if (index < 0 ) {
        //log.log(Level.WARNING, "Column not found - " + columnName);
        return null;
      }

      return this.getValueById(index);
    }

    get_ColumnIndex(propertie: string): number{
      if (propertie){
        return this._m_properties.indexOf(propertie);
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

      if (index < 0 || index >= this._m_values.length)
      {
        return false;
      }

      this._m_values[index] = value;
      return true;
    }
}
