/// <reference types="cypress" />

describe('Register spec', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('registers a new account and redirects to login', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 201,
      body: {},
    }).as('register');

    cy.get('input[formControlName=firstName]').type('Jane');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('jane.doe@yoga.com');
    cy.get('input[formControlName=password]').type(`${'strongPass'}{enter}`);

    cy.wait('@register');
    cy.url().should('include', '/login');
  });

  it('prevents submit when a required field is missing', () => {
    cy.get('input[formControlName=firstName]').type('Jo');
    cy.get('input[formControlName=email]').type('missing-last@yoga.com');

    cy.get('button[type=submit]').should('be.disabled');
  });
});
