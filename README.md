# Yoga App – Test & Run Guide

## Overview
Full-stack yoga booking app with Spring Boot backend and Angular frontend. This guide explains how to set up MySQL, run the app, execute tests, and view coverage (front, back, and e2e) with the 80% target on all indicators.

## Prerequisites
- Java 11 (JDK)
- Node.js 16 + npm
- Angular CLI 14 (`npm install -g @angular/cli@14`)
- MySQL running on port 3306

## Database Setup
1. Start MySQL on port 3306.
2. Create schema and seed data:
   ```sql
   SOURCE ressources/sql/script.sql;
   ```
3. Default admin credentials: `yoga@studio.com / test!1234`.

## Install & Run
### Backend (Spring Boot)
```bash
cd back
mvn clean install
mvn spring-boot:run
```
API listens on `http://localhost:8080`.

### Frontend (Angular)
```bash
cd front
npm install
npm run start
```
App is available at `http://localhost:4200`. Start the backend before opening the UI.

## Usage Checks
- Login as admin: `yoga@studio.com / test!1234`
- Verify admin actions: create, update, delete a session; logout.
- Create a user account, login, and join/leave a session.

## Testing & Coverage
### Backend (JUnit/Mockito + JaCoCo)
```bash
cd back
mvn clean test
mvn verify  # generates JaCoCo report
```
Coverage report: `back/target/site/jacoco/index.html` (DTO/payload classes are excluded per test plan).

### Frontend (Jest)
```bash
cd front
npm run test
```
Coverage report: `front/coverage/jest/lcov-report/index.html`.

### End-to-End (Cypress + NYC)
Start the frontend dev server (`npm run start`) and backend, then run:
```bash
cd front
npm run cypress:run        # headless
# or
npm run cypress:open       # interactive

# Generate e2e coverage after a run
npm run e2e:coverage
```
Coverage report: `front/coverage/lcov-report/index.html`.

## Notes
- Keep MySQL, backend, and frontend running when executing Cypress tests; API calls are mocked in specs.
- If ports differ, update proxies/configs accordingly (`front/proxy.config.json`).
- For a fresh verification, clone the repo into a new directory and replay the steps above.
