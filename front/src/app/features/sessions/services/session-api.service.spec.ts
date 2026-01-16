import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const session: Session = {
    id: 1,
    name: 'Morning Flow',
    description: 'Relax',
    date: new Date('2023-01-01'),
    teacher_id: 1,
    users: []
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should request all sessions', () => {
    service.all().subscribe(response => expect(response).toEqual([session]));

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush([session]);
  });

  it('should request a session detail', () => {
    service.detail('1').subscribe(response => expect(response).toEqual(session));

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(session);
  });

  it('should delete a session', () => {
    service.delete('1').subscribe();

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should create a session', () => {
    service.create(session).subscribe(response => expect(response).toEqual(session));

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(session);
    req.flush(session);
  });

  it('should update a session', () => {
    service.update('1', session).subscribe(response => expect(response).toEqual(session));

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(session);
    req.flush(session);
  });

  it('should participate to a session', () => {
    service.participate('1', '2').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/2');
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should cancel participation to a session', () => {
    service.unParticipate('1', '2').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/2');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});
