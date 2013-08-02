'use strict';

angular.module('clientApp', ['restangular', 'LocalStorageModule'])
  .config(function ($routeProvider, $locationProvider, RestangularProvider) {
    $routeProvider
      .when('/login', {
        templateUrl: 'views/login.html',
        controller: 'LoginCtrl'
      })
      .when('/fb_callback', {
        templateUrl: 'views/facebookLogin.html',
        controller: 'FacebookLoginCtrl'
      })
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });

    $locationProvider.html5Mode(true).hashPrefix('!');

    RestangularProvider.setBaseUrl('http://localhost:8080/service');
    RestangularProvider.setDefaultHttpFields({withCredentials: true});
  });
