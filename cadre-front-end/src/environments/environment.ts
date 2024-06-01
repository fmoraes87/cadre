// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  clientId: 'cadre.api.oauth2-client.ng-cadre-webapp.4feb6a7db5c6621b0b07adc8eb8b64e9',
  clientSecret: 'b9d9f7ab5c06430c176365ba27327bbc',
  server: 'http://localhost:8080' ,
  endpoint_token: 'http://localhost:8080/api/v1/token',
  endpoint_odata: 'http://localhost:8080/ODataServlet.svc/',
  endpoint_user: 'http://localhost:8080/api/v1/user',

};