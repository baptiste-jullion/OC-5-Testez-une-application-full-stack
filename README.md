# Yoga App

## Présentation
Application full-stack de réservation de cours de yoga : backend en Spring Boot et frontend en Angular.

## Prérequis
- Java 11+ (JDK)
- Node.js (>=16) + npm
- Angular CLI 14
- MySQL

## Base de données
1. Démarrer MySQL.
2. Charger le schéma et les données depuis `ressources/sql/script.sql`.
3. Compte administrateur par défaut : `yoga@studio.com / test!1234`.

## Lancer l'application

### Backend
```bash
cd back
mvn clean install
mvn spring-boot:run
```

### Frontend
```bash
cd front
npm install
npm run start
```

## Lancer les tests

### Tests backend
```bash
cd back
mvn clean test
```

### Tests unitaires frontend (Jest)
```bash
cd front
npm test
```

### Tests End-to-End (Cypress)
```bash
cd front
npm run e2e:ci
```

## Générer les rapports de couverture

### Backend (JaCoCo)
```bash
cd back
mvn clean test jacoco:report
```

### Frontend (Jest)
```bash
cd front
npm test -- --coverage --coverageDirectory=coverage
```

### E2E (Cypress + nyc)
```bash
cd front
npm run e2e:coverage
```
