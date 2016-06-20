
var loginController = angular.module('chat.home.controller', []);
loginController.controller('homeCnt', homeCnt)


function homeCnt($scope, $state, loginService, messageService) {



    $scope.logout = _logout;
    $scope.login = _login;

    function _logout() {
        loginService.logout().then(successLogout);
    }

    function _login() {
        loginService.openGoogleAutenticateByPopup().then(successLoginGoogle);
    }

    function successLogout() {
        messageService.close();
        $state.go('home')
    }

    function successLoginGoogle() {
        $state.go('messages')
    }

}