import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log user in and expose observable state', (done) => {
    const sessionInformation: SessionInformation = {
      id: 42,
      admin: true,
      firstName: 'Test',
      lastName: 'User',
      username: 'test@yoga.com',
      token: 'token',
      type: 'Bearer'
    };

    service.logIn(sessionInformation);

    expect(service.sessionInformation).toEqual(sessionInformation);
    expect(service.isLogged).toBeTruthy();

    service.$isLogged().subscribe(value => {
      expect(value).toBeTruthy();
      done();
    });
  });

  it('should log user out and reset the session', () => {
    service.sessionInformation = {
      id: 1,
      admin: false,
      firstName: 'Jane',
      lastName: 'Doe',
      username: 'jane@yoga.com',
      token: 'token',
      type: 'Bearer'
    };
    service.isLogged = true;

    service.logOut();

    expect(service.sessionInformation).toBeUndefined();
    expect(service.isLogged).toBeFalsy();
  });
});
