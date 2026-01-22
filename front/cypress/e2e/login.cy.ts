/// <reference types="cypress" />

describe('Login spec', () => {
  const userResponse = {
    id: 1,
    username: 'userName',
    firstName: 'firstName',
    lastName: 'lastName',
    admin: true,
    token: 'token',
    type: 'Bearer',
  };

  it('logs in successfully', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 200,
      body: userResponse,
    }).as('login');

    cy.intercept('GET', '/api/session', []).as('sessions');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234');
    cy.get('button[type=submit]').click();

    cy.wait('@login');
    cy.wait('@sessions');
    cy.url().should('include', '/sessions');
  });

  it('shows an error when credentials are invalid', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {},
    }).as('loginFail');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('wrong@yoga.com');
    cy.get('input[formControlName=password]').type('bad');
    cy.get('button[type=submit]').click();

    cy.wait('@loginFail');
    cy.get('.error').should('contain', 'An error occurred');
  });

  it('keeps submit disabled while required fields are missing', () => {
    cy.visit('/login');
    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=email]').type('invalid');
    cy.get('button[type=submit]').should('be.disabled');

    cy.get('input[formControlName=password]').type('12');
    cy.get('button[type=submit]').should('be.disabled');
  });
});