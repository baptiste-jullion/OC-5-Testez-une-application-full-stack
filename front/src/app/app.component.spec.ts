import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';

import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';


describe('AppComponent', () => {
  let sessionServiceMock: { $isLogged: jest.Mock; logOut: jest.Mock };
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let router: Router;

  beforeEach(async () => {
    sessionServiceMock = {
      $isLogged: jest.fn().mockReturnValue(of(true)),
      logOut: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [{ provide: SessionService, useValue: sessionServiceMock }]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should expose the session observable', (done) => {
    component.$isLogged().subscribe(value => {
      expect(value).toBeTruthy();
      expect(sessionServiceMock.$isLogged).toHaveBeenCalled();
      done();
    });
  });

  it('should logout and navigate home', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.logout();

    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  });
});
