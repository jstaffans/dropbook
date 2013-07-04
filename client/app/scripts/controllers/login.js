'use strict';

angular.module('clientApp')
  .controller('LoginCtrl', function ($scope, $cookies, Restangular) {
  	var user = Restangular.all('user');

  	$scope.register = function() {
  		user.customPOST('register', {}, {}, $scope.user).then(function(response) {
  			console.log("Created");
  			console.log(response.headers('JSESSIONID'));
  			console.log($cookies);
  		});
  	}

  	$scope.login = function() {
  		var login = Restangular.one('user', 'login');
  		login.post($scope.user);
  	}
  });
