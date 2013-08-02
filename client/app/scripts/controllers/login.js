'use strict';

angular.module('clientApp')
  .controller('LoginCtrl', function ($scope, $location, Restangular, userService) {
    var users = Restangular.all('user');

    var handleError = function(data) {
      $scope.message = 'Error: ' + data;
    };

    $scope.register = function() {
      users.customPOST('register', {}, {}, $scope.user).then(function() {
        $scope.message = 'Successfully registered! You can now log in.';
      }, function(response) {
        handleError(response.data);
      });
    };

    $scope.login = function() {
      users.customPOST('login', {}, {}, $scope.user).then(function (response) {
        userService.setCurrentUser(response.username);
        $location.path('/');
      }, function(response) {
        handleError(response.data);
      });
    };

  });
