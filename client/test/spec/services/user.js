'use strict';

describe('Service: user', function () {

  // load the service's module
  beforeEach(module('clientApp'));

  // instantiate service
  var user;
  beforeEach(inject(function (_userService_) {
    user = _userService_;
  }));

  it('should do something', function () {
    expect(!!user).toBe(true);
  });

});
