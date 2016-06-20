(function () {
    angular.module('chat.message.controller', []).controller('messageCnt', messageController);

    function messageController($scope, messageService, $rootScope, $sce) {


        $scope.users = [];
        $scope.message = '';
        $scope.messageShow = '';
        $scope.sendMessage = _sendMessage;

        openWs();

        function onMessage(message) {
            var newMessage = JSON.parse(message);

            if (newMessage.message != null) {
                $scope.messageShow += $sce.trustAsHtml('\n' + newMessage.message);
            } else {
                updateUser(newMessage.users);
            }
        }

        function updateUser(users) {
            $scope.users = users;
        }

        function _sendMessage() {
            console.log($scope.message);
            messageService.sendMessage($scope.message);
            $scope.message = '';
        }

        function openWs() {
            messageService.openWs(sendInfo).then(null, failOpen, onMessage);
        }

        function sendInfo() {
            messageService.sendUserInfo($rootScope.userDetails.userName, $rootScope.userDetails.pictureSrc);
        }

        function failOpen() {
            alert('falaha ao concetar no servidor.');
        }

    }


})();