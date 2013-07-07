'use strict';

angular.module('clientApp', ['ngCookies', 'restangular'])
  .config(function ($routeProvider, $locationProvider, RestangularProvider) {
    $routeProvider
      .when('/login', {
        templateUrl: 'views/login.html',
        controller: 'LoginCtrl'
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
    RestangularProvider.setErrorInterceptor(function(response) {
      if (response.status === 403) {
        window.location = '/login';
      }
    });
  });
