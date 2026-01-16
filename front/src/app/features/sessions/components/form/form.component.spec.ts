import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { FormComponent } from './form.component';
import { Session } from '../../interfaces/session.interface';
import { ActivatedRoute } from '@angular/router';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiService: SessionApiService;
  let router: Router;
  let snackBar: MatSnackBar;

  const sessionServiceMock = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  const activatedRouteMock = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  } as unknown as ActivatedRoute;

  const teacherServiceMock = {
    all: jest.fn().mockReturnValue(of([]))
  };

  const sessionApiServiceMock = {
    create: jest.fn().mockReturnValue(of({} as Session)),
    update: jest.fn().mockReturnValue(of({} as Session)),
    detail: jest.fn().mockReturnValue(of({
      name: 'Existing',
      description: 'desc',
      date: new Date('2023-01-01'),
      teacher_id: 9,
      users: []
    }))
  };

  const snackBarMock = {
    open: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatButtonModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule
      ],
      declarations: [FormComponent],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    router = TestBed.inject(Router);
    snackBar = TestBed.inject(MatSnackBar);

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect non admin users to sessions', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    sessionServiceMock.sessionInformation.admin = false;

    component.ngOnInit();

    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);

    sessionServiceMock.sessionInformation.admin = true;
  });

  it('should create a session when form is valid', () => {
    component.sessionForm?.setValue({
      name: 'Morning',
      date: new Date('2023-01-01').toISOString().split('T')[0],
      teacher_id: 1,
      description: 'Start your day right'
    });

    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.submit();

    expect(sessionApiService.create).toHaveBeenCalled();
    expect(snackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should switch to update mode and call update endpoint', () => {
    const originalRouter = (component as any).router;
    const customRouter = {
      url: '/sessions/update/1',
      navigate: jest.fn()
    } as Partial<Router> as Router;

    (component as any).router = customRouter;

    component.ngOnInit();
    fixture.detectChanges();

    component.sessionForm?.setValue({
      name: 'Updated',
      date: new Date('2023-02-02').toISOString().split('T')[0],
      teacher_id: 2,
      description: 'Updated session'
    });

    component.submit();

    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
    expect(sessionApiService.update).toHaveBeenCalledWith('1', expect.objectContaining({ name: 'Updated' }));
    expect(snackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });

    (component as any).router = originalRouter;
  });
});
