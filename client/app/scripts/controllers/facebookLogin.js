'use strict';

angular.module('clientApp')
  .controller('FacebookLoginCtrl', function ($scope, $routeParams, Restangular) {
    var users = Restangular.all('user');
    var code = $routeParams.code;

    users.customPOST('login', {}, {}, {code: code}).then(function (response) {
      console.log(response);
    });

  });
