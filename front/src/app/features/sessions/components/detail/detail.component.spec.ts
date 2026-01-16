import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';

import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from '../../../../interfaces/teacher.interface';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionApiService: SessionApiService;
  let router: Router;
  let matSnackBar: MatSnackBar;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  const mockRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  } as unknown as ActivatedRoute;

  const session: Session = {
    id: 1,
    name: 'Morning Flow',
    description: 'Relaxing class',
    date: new Date('2023-01-01'),
    teacher_id: 99,
    users: [1, 2],
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-01-02')
  };

  const teacher: Teacher = {
    id: 99,
    firstName: 'Jane',
    lastName: 'Doe',
    createdAt: new Date('2023-01-01'),
    updatedAt: new Date('2023-01-02')
  };

  const sessionApiServiceMock = {
    detail: jest.fn().mockReturnValue(of(session)),
    delete: jest.fn().mockReturnValue(of(undefined)),
    participate: jest.fn().mockReturnValue(of(undefined)),
    unParticipate: jest.fn().mockReturnValue(of(undefined))
  };

  const teacherServiceMock = {
    detail: jest.fn().mockReturnValue(of(teacher))
  };

  const snackBarMock = {
    open: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatButtonModule,
        MatCardModule,
        MatIconModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: ActivatedRoute, useValue: mockRoute },
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: MatSnackBar, useValue: snackBarMock }
      ],
    }).compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch session and teacher on init', () => {
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    expect(component.session).toEqual(session);
    expect(component.teacher).toEqual(teacher);
    expect(component.isParticipate).toBeTruthy();
    expect(component.isAdmin).toBeTruthy();
  });

  it('should delete session and redirect to list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.delete();

    expect(sessionApiService.delete).toHaveBeenCalledWith('1');
    expect(matSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should re-fetch session when participating', () => {
    component.participate();

    expect(sessionApiService.participate).toHaveBeenCalledWith('1', mockSessionService.sessionInformation.id.toString());
    expect(sessionApiService.detail).toHaveBeenCalledTimes(2);
  });

  it('should re-fetch session when leaving', () => {
    component.unParticipate();

    expect(sessionApiService.unParticipate).toHaveBeenCalledWith('1', mockSessionService.sessionInformation.id.toString());
    expect(sessionApiService.detail).toHaveBeenCalledTimes(2);
  });
});

