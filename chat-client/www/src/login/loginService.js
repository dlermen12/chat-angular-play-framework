(function () {

    angular.module('chat.login.service', ['googleOauth']).service("loginService", loginService);

    function loginService($http, $q, Token, $rootScope, messageService) {

        return ({
            openGoogleAutenticateByPopup: _openGoogleAutenticateByPopup,
            getUserInfo: loadUserInfo,
            logout: _logout,
            getAccessToken: _getAccessToken
        });

        function _logout() {
            return $q(function (resolve, reject) {
                $rootScope.userDetails = null;
                localStorage.accessToken = null;
                resolve();
            });
        }

        function _getAccessToken() {
            return localStorage.accessToken;
        }

        function _openGoogleAutenticateByPopup() {
            var deferred = $q.defer();
            Token.getTokenByPopup({}).then(getTokenByPopUp, failGetTokenByPopup);


            return deferred.promise;


            function getTokenByPopUp(params) {
                Token.verifyAsync(params.access_token).then(successVerifyToken, failVeryToken);

                function successVerifyToken(data) {
                    loadUserInfo(params.access_token).then(function () {
                        deferred.resolve();
                    });

                    $rootScope.$apply(function () {
                        Token.set(params.access_token);
                    });
                }
            }
        }

        function failVeryToken() {
            alert("Failed to verify token.");
        }

        function loadUserInfo(acessToken) {
            var request = $http({
                method: "GET",
                url: "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + acessToken
            });

            return (request.then(successLoadUserInfo, handleError));
        }

        function failGetTokenByPopup() {
            alert("Failed to get token from popup.");
        }

        function successLoadUserInfo(response) {
            var data = response.data;
            $rootScope.userDetails = {};
            $rootScope.userDetails.userName = data.name;
            $rootScope.userDetails.pictureSrc = data.picture;
        }

        function handleError(response) {
            return ($q.reject(response));
        }

        function handleSuccess(response) {
            return (response.data);
        }
    }

})();

