'use strict';

angular.module('clientApp')
  .controller('MainCtrl', function ($scope, Restangular, userService) {
  	$scope.getNewQuote = function() {
      var wisdomPromise = Restangular.one('wisdom', 'random').get();
      wisdomPromise.then(function (wisdom) {
        $scope.quote = wisdom.quote;
      });
    };

    $scope.logout = function() {
    	userService.logout();
    }

    $scope.getNewQuote();
  });
