
var appChat = angular.module('chat', ['ui.router', 'chat.home', 'chat.login', 'chat.message']);

appChat.config(appConfig);
appChat.run(appRun);


function appRun($rootScope,messageService) {
    $rootScope.baseUrl = "http://localhost:9000/";

    }

function appConfig($urlRouterProvider)
{
    $urlRouterProvider.otherwise("/home");
}