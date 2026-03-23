# Backend - Service Comptable Spring Boot

API REST complète pour la gestion des comptes et transactions comptables.

## Démarrage Rapide

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

## Configuration

Éditer `src/main/resources/application.yml` pour configurer :
- Connexion MySQL
- Port (défaut: 8080)
- Logging
- CORS

## Architecture

```
com.erp.accounting/
├── AccountingServiceApplication.java  # Bootstrap
├── config/                            # Configuration (CORS, etc)
├── controller/                        # REST Endpoints
├── service/                          # Business Logic
├── repository/                       # Data Access Layer
├── entity/                           # JPA Entities
├── dto/                              # Data Transfer Objects
└── exception/                        # Exception Handling
```

## API Endpoints

### Comptes
- `GET /accounts` - Lister tous
- `POST /accounts` - Créer
- `GET /accounts/{id}` - Récupérer
- `PUT /accounts/{id}` - Mettre à jour
- `GET /accounts/{id}/balance` - Solde

### Transactions
- `GET /transactions` - Lister toutes
- `POST /transactions` - Créer
- `GET /transactions/account/{id}` - Par compte
- `PATCH /transactions/{id}/reconcile` - Réconcilier

## Stack

- Spring Boot 3.2
- Java 21
- MySQL 8.0
- JPA/Hibernate
- JDBC pour procédures stockées
- Lombok pour les POJOs

## Procédures Stockées

Toute la logique comptable critique est implémentée en procédures stockées :
- create_account
- create_transaction (mise à jour automatique des soldes)
- get_account_balance
- post_journal_entry (validation et posting)
- reconcile_transaction
- get_monthly_financial_summary

## Tests

```bash
# Exécuter les tests
mvn test

# Avec couverture
mvn clean test jacoco:report
```

## Logs

Les logs sont disponibles dans `logs/accounting-service.log`

Configuration du niveau de log dans `application.yml` :
```yaml
logging:
  level:
    com.erp.accounting: DEBUG
```

## Production

```bash
# Build JAR
mvn clean package

# Exécuter
java -jar target/accounting-service-1.0.0.jar
```

## Intégration

Ce service s'intègre with:
- Module Ventes (enregistrement automatique des revenus)
- Module Achats (enregistrement des dépenses)
- Module RH (paiement des salaires)
- Module Stock (ajustements d'inventaire)

Voir `INTEGRATION.md` pour plus de détails.
