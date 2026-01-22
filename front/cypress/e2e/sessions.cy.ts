/// <reference types="cypress" />

const adminUser = {
  id: 1,
  username: 'admin',
  firstName: 'Admin',
  lastName: 'User',
  admin: true,
  token: 'token',
  type: 'Bearer',
};

const regularUser = {
  id: 2,
  username: 'member',
  firstName: 'Regular',
  lastName: 'User',
  admin: false,
  token: 'token',
  type: 'Bearer',
};

const teachers = [
  { id: 1, firstName: 'John', lastName: 'Doe' },
  { id: 2, firstName: 'Ada', lastName: 'Lovelace' },
];

const baseSessions = [
  {
    id: 1,
    name: 'Morning Flow',
    description: 'A gentle start',
    date: '2023-01-01T10:00:00.000Z',
    teacher_id: 1,
    users: [],
    createdAt: '2023-01-01T09:00:00.000Z',
    updatedAt: '2023-01-01T09:00:00.000Z',
  },
  {
    id: 2,
    name: 'Evening Stretch',
    description: 'Stretch together',
    date: '2023-01-02T18:00:00.000Z',
    teacher_id: 2,
    users: [3],
    createdAt: '2023-01-02T17:00:00.000Z',
    updatedAt: '2023-01-02T17:00:00.000Z',
  },
];

const login = (user = adminUser, sessions = baseSessions) => {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: user,
  }).as('login');

  cy.intercept('GET', '/api/session', sessions).as('getSessions');

  cy.visit('/login');
  cy.get('input[formControlName=email]').type('yoga@studio.com');
  cy.get('input[formControlName=password]').type('test!1234');
  cy.get('button[type=submit]').click();

  cy.wait('@login');
  cy.wait('@getSessions');
};

describe('Sessions flows', () => {
  it('shows admin actions on session list', () => {
    login();

    cy.contains('button', 'Create').should('exist');
    cy.contains('button', 'Edit').should('exist');
    cy.contains('button', 'Detail').first().should('exist');
  });

  it('creates a session as admin', () => {
    const updatedSessions = [
      ...baseSessions,
      {
        id: 3,
        name: 'Power Yoga',
        description: 'Build strength',
        date: '2023-02-01T12:00:00.000Z',
        teacher_id: 1,
        users: [],
        createdAt: '2023-02-01T10:00:00.000Z',
        updatedAt: '2023-02-01T10:00:00.000Z',
      },
    ];

    login(adminUser, baseSessions);

    cy.intercept('GET', '/api/teacher', teachers).as('getTeachers');
    cy.intercept('POST', '/api/session', (req) => {
      expect(req.body.name).to.equal('Power Yoga');
      req.reply({ statusCode: 201, body: updatedSessions[2] });
    }).as('createSession');
    cy.intercept('GET', '/api/session', updatedSessions);

    cy.contains('button', 'Create').click();
    cy.wait('@getTeachers');

    cy.get('input[formControlName=name]').type('Power Yoga');
    cy.get('input[formControlName=date]').type('2023-02-01');
    cy.get('mat-select[formcontrolname=teacher_id]').click();
    cy.get('mat-option').contains('John Doe').click();
    cy.get('textarea[formcontrolname=description]').type('Build strength');

    cy.contains('button', 'Save').click();

    cy.wait('@createSession');
    cy.url().should('include', '/sessions');
    cy.contains('Power Yoga').should('exist');
  });

  it('updates a session', () => {
    const sessionToUpdate = baseSessions[0];
    const updatedSession = { ...sessionToUpdate, description: 'Updated description' };

    login(adminUser, baseSessions);

    cy.intercept('GET', `/api/session/${sessionToUpdate.id}`, sessionToUpdate).as('getSessionDetail');
    cy.intercept('GET', '/api/teacher', teachers).as('getTeachersUpdate');
    cy.intercept('PUT', `/api/session/${sessionToUpdate.id}`, (req) => {
      expect(req.body.description).to.equal('Updated description');
      req.reply({ statusCode: 200, body: updatedSession });
    }).as('updateSession');

    cy.contains('button', 'Edit').first().click();

    cy.wait('@getSessionDetail');
    cy.wait('@getTeachersUpdate');

    cy.get('textarea[formcontrolname=description]').clear().type('Updated description');
    cy.contains('button', 'Save').click();

    cy.wait('@updateSession');
    cy.url().should('include', '/sessions');
  });

  it('deletes a session from detail view', () => {
    const sessionToDelete = baseSessions[1];

    login(adminUser, baseSessions);

    cy.intercept('GET', `**/api/session/${sessionToDelete.id}`, sessionToDelete).as('getSessionForDelete');
    cy.intercept('GET', `**/api/teacher/${sessionToDelete.teacher_id}`, teachers[1]).as('getTeacherForDelete');
    cy.intercept('DELETE', `**/api/session/${sessionToDelete.id}`, { statusCode: 200 }).as('deleteSession');

    cy.contains('mat-card', sessionToDelete.name)
      .find('button')
      .contains('Detail')
      .click();

    cy.wait('@getSessionForDelete');
    cy.wait('@getTeacherForDelete');
    cy.contains(sessionToDelete.name).should('exist');
    cy.contains('button', 'Delete').click();

    cy.wait('@deleteSession');
    cy.url().should('include', '/sessions');
  });

  it('lets a user participate then leave a session', () => {
    let isParticipating = false;
    const sessionForUser = { ...baseSessions[0] };

    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: regularUser,
    }).as('loginUser');
    cy.intercept('GET', '/api/session', [sessionForUser]).as('getSessionsUser');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('member@yoga.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();
    cy.wait('@loginUser');
    cy.wait('@getSessionsUser');

    cy.intercept('GET', `/api/session/${sessionForUser.id}`, (req) => {
      const users = isParticipating ? [regularUser.id] : [];
      req.reply({ ...sessionForUser, users });
    }).as('getSessionUser');
    cy.intercept('GET', `/api/teacher/${sessionForUser.teacher_id}`, teachers[0]).as('getTeacherUser');
    cy.intercept('POST', `/api/session/${sessionForUser.id}/participate/${regularUser.id}`, (req) => {
      isParticipating = true;
      req.reply({ statusCode: 200 });
    }).as('participate');
    cy.intercept('DELETE', `/api/session/${sessionForUser.id}/participate/${regularUser.id}`, (req) => {
      isParticipating = false;
      req.reply({ statusCode: 200 });
    }).as('unParticipate');

    cy.contains('button', 'Detail').first().click();
    cy.wait('@getSessionUser');
    cy.wait('@getTeacherUser');

    cy.contains('button', 'Participate').click();
    cy.wait('@participate');
    cy.wait('@getSessionUser');

    cy.contains('button', 'Do not participate').click();
    cy.wait('@unParticipate');
    cy.wait('@getSessionUser');
  });
});
