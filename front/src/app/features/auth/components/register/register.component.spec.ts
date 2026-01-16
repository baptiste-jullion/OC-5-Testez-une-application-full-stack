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

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let router: Router;

  const authServiceMock = {
    register: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,  
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [{ provide: AuthService, useValue: authServiceMock }]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should invalidate form when mandatory fields are missing', () => {
    expect(component.form.invalid).toBeTruthy();

    component.form.setValue({
      email: 'test@yoga.com',
      firstName: 'Test',
      lastName: 'User',
      password: 'secret'
    });

    expect(component.form.valid).toBeTruthy();
  });

  it('should register user and navigate to login page', () => {
    authServiceMock.register.mockReturnValue(of(undefined));
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    const payload = {
      email: 'new@yoga.com',
      firstName: 'New',
      lastName: 'User',
      password: 'secret'
    };
    component.form.setValue(payload);

    component.submit();

    expect(authServiceMock.register).toHaveBeenCalledWith(payload);
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBeFalsy();
  });

  it('should expose an error when registration fails', () => {
    authServiceMock.register.mockReturnValue(throwError(() => new Error('Duplicate')));
    const payload = {
      email: 'existing@yoga.com',
      firstName: 'Existing',
      lastName: 'User',
      password: 'secret'
    };
    component.form.setValue(payload);

    component.submit();

    expect(component.onError).toBeTruthy();
  });
});
