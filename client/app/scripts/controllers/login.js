'use strict';

angular.module('clientApp')
  .controller('LoginCtrl', function ($scope, userService) {
    $scope.register = function() {
      userService.register($scope.user);
    };

    $scope.login = function() {
      userService.login($scope.user);
    };
  });
