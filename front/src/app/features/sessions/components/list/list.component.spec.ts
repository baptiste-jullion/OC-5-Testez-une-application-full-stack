import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';

import { ListComponent } from './list.component';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  };

  const mockSessions: Session[] = [
    {
      id: 1,
      name: 'Morning Flow',
      description: 'Start the day right',
      date: new Date('2023-01-01T08:00:00Z'),
      teacher_id: 1,
      users: []
    },
    {
      id: 2,
      name: 'Evening Chill',
      description: 'Relax your body',
      date: new Date('2023-01-02T18:00:00Z'),
      teacher_id: 2,
      users: [1]
    }
  ];

  const sessionApiServiceMock = {
    all: jest.fn().mockReturnValue(of(mockSessions))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatButtonModule,
        MatCardModule,
        MatIconModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: sessionApiServiceMock }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should expose the fetched sessions as cards', () => {
    const cards = fixture.debugElement.queryAll(By.css('.item'));
    expect(cards.length).toBe(mockSessions.length);
  });

  it('should display admin actions when user is admin', () => {
    const buttons = Array.from(fixture.nativeElement.querySelectorAll('button')) as HTMLButtonElement[];
    const createButton = buttons.find(button => button.textContent?.includes('Create'));
    const editButtons = buttons.filter(button => button.textContent?.includes('Edit'));

    expect(createButton).toBeDefined();
    expect(editButtons.length).toBe(mockSessions.length);
  });

  it('should hide admin actions for non admin users', () => {
    mockSessionService.sessionInformation.admin = false;
    fixture.detectChanges();

    const buttons = Array.from(fixture.nativeElement.querySelectorAll('button')) as HTMLButtonElement[];
    const createButton = buttons.find(button => button.textContent?.includes('Create'));
    const editButtons = buttons.filter(button => button.textContent?.includes('Edit'));

    expect(createButton).toBeUndefined();
    expect(editButtons.length).toBe(0);
  });
});
