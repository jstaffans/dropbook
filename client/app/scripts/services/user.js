'use strict';

angular.module('clientApp')
  .service('userService', function (Restangular) {
    var user = Restangular.all('user');

    var register = function(u) {
      user.customPOST('register', {}, {}, u);
    };

    var login = function(u) {
      user.customPOST('login', {}, {}, u);
    };

    var logout = function() {
      user.customGET('logout');
    };

    return {
      login: login,
      logout: logout,
      register: register
    };
  });
