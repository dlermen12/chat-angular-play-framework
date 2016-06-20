(function () {

    var appLogin = angular.module('chat.home', ['chat.home.controller']);
    appLogin.config(appConfig);

    function appConfig($stateProvider) {
        $stateProvider
                .state('home', {
                    url: "/home",
                    templateUrl: "src/home/home.html",
                    controller: 'homeCnt as cnt'
                });
    }

})();
