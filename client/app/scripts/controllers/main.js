'use strict';

angular.module('clientApp')
  .controller('MainCtrl', function ($scope, Restangular) {
  	$scope.getNewQuote = function() {
      var wisdomPromise = Restangular.one('wisdom', 'random').get();
      wisdomPromise.then(function (wisdom) {
        $scope.quote = wisdom.quote;
      });
    };

    $scope.getNewQuote();
  });
