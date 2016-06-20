(function () {
    angular.module('chat.message.service', []).service("messageService", messageService);
    function messageService($q, $rootScope) {

        var webSocket = null;
        return ({
            openWs: _ws,
            sendMessage: _sendMessage,
            sendUserInfo: _sendUserInfo,
            close: _close
        });


        function _close() {
            if (webSocket != null)
                webSocket.close()
        }

        function _sendUserInfo(userName, userPicture, userId) {
            var userInfo = {"userName": userName, "userPicture": userPicture, "userId": userId};
            _send(JSON.stringify(userInfo));
        }

        function _sendMessage(text) {
            var message = {"message": text};
            _send(JSON.stringify(message));
        }

        function _send(message) {
            webSocket.send(message);
        }

        function _ws(onOpen) {
            var deferred = $q.defer();
            webSocket = new WebSocket($rootScope.baseUrl.replace("http", 'ws') + "ws");

            webSocket.onmessage = function (event) {
                deferred.notify(event.data);
            };

            webSocket.onopen = function () {
                onOpen();
            };

            return deferred.promise;
        }
    }

})();

