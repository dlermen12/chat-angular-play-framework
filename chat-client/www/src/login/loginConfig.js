
var appLogin = angular.module('chat.login', ['chat.login.service']);

appLogin.config(appConfig);
appLogin.run(onRun);



function onRun($state, loginService) {

    var acessToken = loginService.getAccessToken();
    if (acessToken == null) {
        $state.go('home')
    } else {
        loginService.getUserInfo(acessToken).then(
                function () {
                    $state.go('messages')
                },
                function () {
                    $state.go('home')
                });
    }
}


function appConfig(TokenProvider) {
    TokenProvider.extendConfig({
        clientId: '952712433878-26af2rf3cdf93i2cocvorbe9ip4f89ln.apps.googleusercontent.com',
        redirectUri: 'http://localhost:8080/oauth2callback.html', // allow lunching demo from a mirror
        scopes: ["https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile", "https://www.googleapis.com/auth/plus.me"]
    });

}