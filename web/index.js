// <<<<<<< HEAD
// var AWS = require('aws-sdk');
// var cmd = require('node-cmd');
// var CognitoSDK = require('amazon-cognito-identity-js-node')
// var username =  object.getElementsByClassName('username').value;
// var password = object.getElementsByClassName('password').value;
// var cognitoUser = new AWSCognito.CognitoIdentityServiceProvider.CognitoUser(userData);
// var authenticationData = {
//     Username : username,
//     Password : password
// };
// var authenticationDetails = new AWSCognito.CognitoIdentityServiceProvider.AuthenticationDetails(authenticationData);
// var poolData = {
//     UserPoolId : 'us-east-1_3CcK6s7AN', // Your user pool id here
//     ClientId : '415ll1cqlgd38gsu2c4gmha8m' // Your client id here
// };
// var userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);
// var userData = {
//     Username : username,
//     Pool : userPool
// };
// var cognitoUser = new AWSCognito.CognitoIdentityServiceProvider.CognitoUser(userData);
// cognitoUser.authenticateUser(authenticationDetails, {
//     onSuccess: function hello (result) {
//         console.log('access token + ' + result.getAccessToken().getJwtToken());
//
//         //POTENTIAL: Region needs to be set if not already set previously elsewhere.
//         AWS.config.region = 'us-east-1';
//
//         AWS.config.credentials = new AWS.CognitoIdentityCredentials({
//             IdentityPoolId : '415ll1cqlgd38gsu2c4gmha8m', // your identity pool id here
//             Logins : {
//                 // Change the key below according to the specific region your user pool is in.
//                 'cognito-idp.us-east-1.amazonaws.com/us-east-1_3CcK6s7AN' : result.getIdToken().getJwtToken()
//             }
//         });
//
//         //refreshes credentials using AWS.CognitoIdentity.getCredentialsForIdentity()
//         AWS.config.credentials.refresh((error) => {
//             if (error) {
//                 console.error(error);
//             } else {
//                 // Instantiate aws sdk service objects now that the credentials have been updated.
//                 // example: var s3 = new AWS.S3();
//                 console.log('Successfully logged!');
//             }
//         });
//     },
//
//     onFailure: function(err) {
//         alert(err);
//     },
//
// });
//
// function exitApp() {
//     window.location.href="./signin.html";
//     cognitoUser.globalSignOut();
// }
// const aws = require('aws-sdk');
// const docClient = new AWS.DynamoDB.DocumentClient({region: 'us-east-1'});
//
// exports.handle()=function(e,ctx,callback){
//     let scanningParameters = {
//         TableName:'reports',
//         Limit:1
//     };
//     docClient.scan(scanningParameters, function(err,data){
//         if(err){
//             callback(err,null);
//         }else{
//             callback(null,data);
//         }
//     });
// }
//
function signOut() {
    window.location.href="./signin.html";
}

function signIn(){
    window.location.href="./index.html";
}
