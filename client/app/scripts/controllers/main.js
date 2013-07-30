'use strict';

angular.module('clientApp')
  .controller('MainCtrl', function ($scope, $location, Restangular, userService) {
    $scope.quoteLoaded = function() {
      return ($scope.quote !== undefined);
    }

    $scope.getNewQuote = function() {
      Restangular.one('wisdom', 'random').get().then(function (wisdom) {
        $scope.quote = wisdom.quote;
      });
    };

    $scope.logout = function() {
      userService.logout().then(function() { $location.path('/login') });
    }

    $scope.getNewQuote();
  });
