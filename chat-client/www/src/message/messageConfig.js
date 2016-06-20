
(function () {
    angular.module('chat.message', ['chat.message.controller','chat.message.service']).config(appConfig);

    function appConfig($stateProvider) {
        $stateProvider
                .state('messages', {
                    url: "/messages",
                    templateUrl: "src/message/message.html",
                    controller: 'messageCnt as cnt'
                })
    }
})();