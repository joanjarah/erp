# Module de ComptabilitÃ© - ERP

Module complet de comptabilitÃ© pour ERP destinÃ© aux startups et petites entreprises.

## ðŸ“‹ Structure du Projet

```
Compta/
â”œâ”€â”€ database/
â”‚   â”œâ”€â”€ schema.sql          # CrÃ©ation des tables
â”‚   â”œâ”€â”€ procedures.sql      # ProcÃ©dures stockÃ©es
â”‚   â””â”€â”€ seed.sql            # DonnÃ©es initiales
â”œâ”€â”€ backend/
â”‚   â””â”€â”€ accounting-service/ # Application Spring Boot
â””â”€â”€ frontend/
    â””â”€â”€ src/                # Application React
```

## ðŸ—„ï¸ BASE DE DONNÃ‰ES

### Migration SQL

1. **CrÃ©er la base de donnÃ©es :**
```sql
mysql -u root -p < database/schema.sql
```

2. **Ajouter les procÃ©dures stockÃ©es :**
```sql
mysql -u root -p erp_dev < database/procedures.sql
```

3. **Charger les donnÃ©es initiales :**
```sql
mysql -u root -p erp_dev < database/seed.sql
```

### Tables principales

- **accounts** : Comptes comptables
- **transactions** : Enregistrement des dÃ©bits/crÃ©dits
- **journal_entries** : Ã‰critures comptables (double entrÃ©e)
- **journal_entry_lines** : Lignes des Ã©critures
- **account_history** : Historique des soldes
- **chart_of_accounts** : Structure hiÃ©rarchique des comptes

### ProcÃ©dures stockÃ©es

1. `create_account()` - CrÃ©er un compte
2. `list_accounts()` - Lister les comptes
3. `get_account_balance()` - RÃ©cupÃ©rer le solde
4. `create_transaction()` - CrÃ©er une transaction
5. `get_transactions()` - RÃ©cupÃ©rer les transactions
6. `create_journal_entry()` - CrÃ©er une Ã©criture comptable
7. `add_journal_entry_line()` - Ajouter une ligne Ã  une Ã©criture
8. `post_journal_entry()` - Valider et poster une Ã©criture
9. `get_account_statement()` - RelevÃ© de compte
10. `get_monthly_financial_summary()` - RÃ©sumÃ© financier
11. `reconcile_transaction()` - RÃ©concilier une transaction

## ðŸ”§ BACKEND (Spring Boot)

### PrÃ©requis

- Java 21+
- Maven 3.8+
- MySQL 8.0+

### Installation

1. **Naviguer dans le rÃ©pertoire backend :**
```bash
cd backend/accounting-service
```

2. **Configurer la base de donnÃ©es (application.yml) :**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/erp_dev
    username: root
    password: root
```

3. **Compiler et dÃ©marrer :**
```bash
mvn clean install
mvn spring-boot:run
```

Le serveur dÃ©marre sur `http://localhost:8080/api/v1`

### Architecture

```
com.erp.accounting/
â”œâ”€â”€ AccountingServiceApplication.java
â”œâ”€â”€ controller/          # REST Controllers
â”œâ”€â”€ service/            # Business Logic
â”œâ”€â”€ repository/         # Data Access
â”œâ”€â”€ entity/            # JPA Entities
â”œâ”€â”€ dto/               # Data Transfer Objects
â”œâ”€â”€ exception/         # Custom Exceptions
â””â”€â”€ config/            # Configuration
```

### API Endpoints

#### Comptes

| MÃ©thode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/accounts` | Tous les comptes |
| GET | `/accounts/active` | Comptes actifs |
| GET | `/accounts/{id}` | Compte par ID |
| GET | `/accounts/{id}/balance` | Solde du compte |
| POST | `/accounts` | CrÃ©er un compte |
| PUT | `/accounts/{id}` | Mettre Ã  jour |

#### Transactions

| MÃ©thode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/transactions` | Toutes les transactions |
| GET | `/transactions/{id}` | Transaction par ID |
| GET | `/transactions/account/{accountId}` | Transactions du compte |
| GET | `/transactions/account/{accountId}/range` | Par plage de dates |
| POST | `/transactions` | CrÃ©er une transaction |
| PATCH | `/transactions/{id}/reconcile` | RÃ©concilier |

### Exemple de requÃªte

CrÃ©er une transaction :
```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": 1,
    "transaction_type": "DEBIT",
    "amount": 1000.00,
    "reference_number": "FAC-001",
    "description": "Vente produit"
  }'
```

## âš›ï¸ FRONTEND (React)

### PrÃ©requis

- Node.js 16+ et npm/yarn
- React 18+

### Installation

1. **Naviguer dans le rÃ©pertoire frontend :**
```bash
cd frontend
```

2. **Installer les dÃ©pendances :**
```bash
npm install
```

3. **Configurer l'API (si nÃ©cessaire) :**
```bash
cp .env.example .env
# Modifier REACT_APP_API_URL si l'API n'est pas sur http://localhost:8080
```

4. **DÃ©marrer l'application :**
```bash
npm start
```

L'application dÃ©marre sur `http://localhost:3000`

### Structure

```
frontend/src/
â”œâ”€â”€ pages/               # Pages principales
â”œâ”€â”€ components/         # Composants rÃ©utilisables
â”œâ”€â”€ services/           # Services API
â”œâ”€â”€ hooks/             # Custom hooks
â”œâ”€â”€ styles/            # Feuilles CSS
â””â”€â”€ App.js             # Composant principal
```

### Pages

1. **Dashboard** - Vue d'ensemble des finances
2. **Accounts** - Gestion des comptes
3. **Transactions** - Journal des transactions

### Composants

- `AccountList` - Liste des comptes avec filtres
- `CreateAccountForm` - Formulaire de crÃ©ation de compte
- `TransactionList` - Historique des transactions
- `CreateTransactionForm` - Ajout de transaction

## ðŸ“Š INTÃ‰GRATION AVEC AUTRES MODULES

### Module Ventes

Lors d'une vente confirmÃ©e, ajouter une transaction automatique :

```sql
INSERT INTO transactions (
    account_id,
    transaction_type,
    amount,
    transaction_date,
    reference_number,
    description,
    status,
    created_by
) VALUES (
    (SELECT id FROM accounts WHERE account_number = '4100'),
    'CREDIT',
    amount_sold,
    sale_date,
    invoice_number,
    'Vente ' + invoice_number,
    'POSTED',
    'SYSTEM'
);
```

### Module Achats

Pour un achat effectuÃ© :

```sql
INSERT INTO transactions (
    account_id,
    transaction_type,
    amount,
    transaction_date,
    reference_number,
    description,
    status,
    created_by
) VALUES (
    (SELECT id FROM accounts WHERE account_number = '5100'),
    'DEBIT',
    amount_purchased,
    purchase_date,
    purchase_order,
    'Achat ' + purchase_order,
    'POSTED',
    'SYSTEM'
);
```

### Module RH

Pour le paiement des salaires :

```sql
INSERT INTO transactions (
    account_id,
    transaction_type,
    amount,
    transaction_date,
    reference_number,
    description,
    status,
    created_by
) VALUES (
    (SELECT id FROM accounts WHERE account_number = '5200'),
    'DEBIT',
    total_payroll,
    payroll_date,
    'PAYROLL-' + month_year,
    'Paiement salaires ' + month_year,
    'POSTED',
    'SYSTEM'
);
```

## ðŸ”’ SÃ‰CURITÃ‰

- Validation des entrÃ©es avec Jakarta Validation
- Gestion centralisÃ©e des exceptions
- CORS configurÃ© pour localhost:3000
- Transactions ACID au niveau base de donnÃ©es
- Logs structurÃ©s pour audit

## ðŸ“ˆ FONCTIONNALITÃ‰S

âœ… CrÃ©ation et gestion de comptes comptables
âœ… Enregistrement des transactions (dÃ©bit/crÃ©dit)
âœ… Double entrÃ©e comptable
âœ… RelevÃ© de comptes
âœ… RÃ©conciliation des transactions
âœ… RÃ©sumÃ© financier mensuel
âœ… Historique des soldes
âœ… API REST complÃ¨te
âœ… Interface React moderne
âœ… Gestion centralisÃ©e des erreurs

## ðŸš€ DÃ‰PLOIEMENT

### Production Backend

1. Builder le JAR
```bash
cd backend/accounting-service
mvn clean package
```

2. ExÃ©cuter le JAR
```bash
java -jar target/accounting-service-1.0.0.jar
```

### Production Frontend

1. Build l'application
```bash
cd frontend
npm run build
```

2. DÃ©ployer le dossier `build/` sur un serveur web

## ðŸ“ PLAN COMPTABLE PAR DÃ‰FAUT

| Code | Compte | Type |
|------|--------|------|
| 1010 | Caisse | CASH |
| 1020 | Banque | ASSET |
| 1100 | Comptes Clients | ASSET |
| 1200 | Stock | ASSET |
| 2100 | Comptes Fournisseurs | LIABILITY |
| 2200 | TVA Ã  Payer | LIABILITY |
| 2300 | Dettes Bancaires | LIABILITY |
| 3100 | Capital Social | EQUITY |
| 3200 | RÃ©sultats ReportÃ©s | EQUITY |
| 4100 | Ventes | REVENUE |
| 4200 | Services | REVENUE |
| 5100 | Achats | EXPENSE |
| 5200 | Salaires | EXPENSE |
| 5300 | Loyer | EXPENSE |
| 5400 | Ã‰lectricitÃ© | EXPENSE |

## ðŸ› DÃ‰PANNAGE

### Erreur de connexion Ã  la base de donnÃ©es

VÃ©rifier que MySQL est en cours d'exÃ©cution et les identifiants sont corrects

### CORS Error en frontend

VÃ©rifier que CORS est configurÃ© dans `CorsConfig.java`

### ProcÃ©dures stockÃ©es introuvables

S'assurer que `procedures.sql` a Ã©tÃ© exÃ©cutÃ© sur la base de donnÃ©es

## ðŸ“ž SUPPORT

Pour les questions ou problÃ¨mes, veuillez consulter la documentation ou contacter l'Ã©quipe de dÃ©veloppement.

## ðŸ“„ LICENCE

Ce projet est destinÃ© Ã  usage professionnel uniquement.

#   e r p  
 