import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { AuthService } from './auth.service';


describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call register endpoint with payload', () => {
    const payload: RegisterRequest = {
      email: 'test@example.com',
      password: 'Password123!',
      firstName: 'Ada',
      lastName: 'Lovelace'
    };

    service.register(payload).subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush(null);
  });

  it('should call login endpoint and return session info', () => {
    const payload: LoginRequest = {
      email: 'test@example.com',
      password: 'Password123!'
    };

    const session: SessionInformation = {
      token: 'abc',
      type: 'Bearer',
      id: 1,
      username: 'ada',
      firstName: 'Ada',
      lastName: 'Lovelace',
      admin: false
    };

    service.login(payload).subscribe(response => {
      expect(response).toEqual(session);
    });

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush(session);
  });
});
