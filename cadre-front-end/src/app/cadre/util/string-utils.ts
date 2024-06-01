export class StringUtils {

    static concat (array: string[], separator: string) : string{
        if (array && array.length){
            return StringUtils.join(array, separator, 0, array.length);
        }

        return undefined;
    }

    static join (array: string[], separator: string, startIndex: number, endIndex: number) : string {
        if (!array || !array.length){
            return undefined;
        }

        let noOfItems: number = endIndex - startIndex;
        if (noOfItems <= 0) {
            return undefined;
        }

        let i;
        let buf = "";
        for (i = 0; i < array.length; i++) { 
            if (array[i]) {
                buf+=(array[i]);
                if (i<array.length-1){
                    buf+=(separator);
                }
            }
        }

        return buf;
    }

    static lpad(str:string, size:number, pad: string): string {
        if (str){
            let pads = size - str.length;
            if (pads <= 0) {
                return str;
            }

            let s = str;
            while (s.length < size){
                s = pad + s;
            }
            return s;
        }

        return str;

    }


}