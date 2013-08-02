'use strict';

angular.module('clientApp')
  .controller('MainCtrl', function ($scope, $location, Restangular, userService) {
    $scope.quoteLoaded = function() {
      return ($scope.quote !== undefined);
    };

    $scope.getNewQuote = function() {
      Restangular.one('wisdom', 'random').get().then(function (wisdom) {
        $scope.quote = wisdom.quote;
      }, function(response) {
        $location.path('/login');
      });
    };

    $scope.logout = function() {
      Restangular.all('user').customGET('logout').then(function () {
        userService.clearCurrentUser();
        $location.path('/login');
      });
    };

    $scope.$watch(userService.getCurrentUser, function(newValue) {
      if (newValue && newValue !== $scope.username) {
        $scope.username = newValue;
      }
    });

    $scope.getNewQuote();
  });
