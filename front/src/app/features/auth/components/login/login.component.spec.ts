import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;

  const authServiceMock = {
    login: jest.fn()
  };

  const sessionServiceMock = {
    logIn: jest.fn()
  } as Partial<SessionService> as SessionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should keep form invalid when fields are empty', () => {
    expect(component.form.invalid).toBeTruthy();

    component.form.setValue({ email: 'yoga@studio.com', password: 'test!1234' });

    expect(component.form.valid).toBeTruthy();
  });

  it('should log user in and redirect to sessions on success', () => {
    const payload = { email: 'yoga@studio.com', password: 'test!1234' };
    const sessionInformation: SessionInformation = {
      id: 1,
      admin: true,
      firstName: 'Yoga',
      lastName: 'Studio',
      token: 'token',
      type: 'Bearer',
      username: payload.email
    };
    authServiceMock.login.mockReturnValue(of(sessionInformation));
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    const logInSpy = jest.spyOn(sessionService, 'logIn');

    component.form.setValue(payload);
    component.submit();

    expect(authServiceMock.login).toHaveBeenCalledWith(payload);
    expect(logInSpy).toHaveBeenCalledWith(sessionInformation);
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBeFalsy();
  });

  it('should expose an error state when authentication fails', () => {
    authServiceMock.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));
    const payload = { email: 'test@test.com', password: '123456' };
    component.form.setValue(payload);

    component.submit();

    expect(authServiceMock.login).toHaveBeenCalledWith(payload);
    expect(component.onError).toBeTruthy();
  });
});
