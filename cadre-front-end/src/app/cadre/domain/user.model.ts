export class UserModel {
    constructor(
        public email: string,
        private _token: string,
        private _refreshToken: string,
        private _tokenExpirationDate: Date,
        public ad_user_id: number,
        public ad_client_id: number,
        public ad_org_id: number,
        public ad_tree_id: number

    ) { }

    get token() {
        if (this._tokenExpirationDate && this._tokenExpirationDate > new Date()) {
            return this._token
        } else {
            return null;
        }
    }

    get refreshToken(){
       return this._refreshToken;
    }
}