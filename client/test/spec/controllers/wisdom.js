'use strict';

describe('Controller: WisdomCtrl', function () {

  // load the controller's module
  beforeEach(module('clientApp'));

  var WisdomCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    WisdomCtrl = $controller('WisdomCtrl', {
      $scope: scope
    });
  }));

});
