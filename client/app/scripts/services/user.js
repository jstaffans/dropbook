'use strict';

angular.module('clientApp')
  .service('userService', function (localStorageService) {
    var setCurrentUser = function(username) {
      localStorageService.clearAll();
      localStorageService.add('currentUser', username);
    };

    var getCurrentUser = function() {
      return localStorageService.get('currentUser');
    };

    var clearCurrentUser = function() {
      localStorageService.clearAll();
    };

    return {
      setCurrentUser: setCurrentUser,
      getCurrentUser: getCurrentUser,
      clearCurrentUser: clearCurrentUser
    };
  });
