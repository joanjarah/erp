# ðŸš€ DÃ‰MARRAGE RAPIDE - MODULE COMPTABILITÃ‰

## PrÃ©requis

- Java 21+
- Maven 3.8+
- Node.js 16+ et npm
- MySQL 8.0+

## 1ï¸âƒ£ SETUP BASE DE DONNÃ‰ES

### Ã‰tape 1 : CrÃ©er la base de donnÃ©es

```bash
# Ouvrir MySQL
mysql -u root -p

# ExÃ©cuter le schÃ©ma
mysql -u root -p < database/schema.sql
mysql -u root -p < database/procedures.sql
mysql -u root -p < database/seed.sql
```

### VÃ©rification

```bash
mysql -u root -p erp_dev -e "SELECT COUNT(*) FROM accounts;"
# RÃ©sultat attendu : 22 comptes
```

## 2ï¸âƒ£ DÃ‰MARRER LE BACKEND

### Ã‰tape 1 : Configuration

```bash
cd backend/accounting-service
# Ã‰diter src/main/resources/application.yml si nÃ©cessaire
# VÃ©rifier : spring.datasource.url, username, password
```

### Ã‰tape 2 : Build et Run

```bash
# Compiler
mvn clean install

# DÃ©marrer le serveur
mvn spring-boot:run
```

**âœ… Vous verrez :** `Started AccountingServiceApplication`

**ðŸŒ API disponible sur :** `http://localhost:8080/api/v1`

### Test rapide

```bash
curl http://localhost:8080/api/v1/accounts
```

## 3ï¸âƒ£ DÃ‰MARRER LE FRONTEND

### Ã‰tape 1 : Installation

```bash
cd frontend

# Installer les dÃ©pendances
npm install

# Configurer l'API (optionnel)
cp .env.example .env
```

### Ã‰tape 2 : DÃ©marrer dÃ©veloppement

```bash
npm start
```

**âœ… Vous verrez :** `Compiled successfully!`

**ðŸŒ Application disponible sur :** `http://localhost:3000`

## 4ï¸âƒ£ PREMIERS PAS

### Via l'interface web

1. Ouvrir `http://localhost:3000`
2. Aller sur l'onglet **Comptes**
3. Voir la liste des comptes par dÃ©faut
4. CrÃ©er un nouveau compte
5. Aller sur l'onglet **Transactions**
6. Ajouter une transaction

### Via l'API (cURL)

```bash
# 1. RÃ©cupÃ©rer tous les comptes
curl -X GET http://localhost:8080/api/v1/accounts

# 2. CrÃ©er une transaction
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "account_id": 1,
    "transaction_type": "DEBIT",
    "amount": 500.00,
    "reference_number": "TEST-001",
    "description": "Test transaction"
  }'

# 3. RÃ©cupÃ©rer les transactions
curl -X GET http://localhost:8080/api/v1/transactions

# 4. VÃ©rifier le solde d'un compte
curl -X GET http://localhost:8080/api/v1/accounts/1/balance
```

## âš¡ COMMANDES UTILES

### Backend

```bash
# Clean build
mvn clean install

# Build sans tests
mvn clean install -DskipTests

# ExÃ©cuter les tests
mvn test

# GÃ©nÃ©rer un rapport de couverture
mvn clean test jacoco:report
```

### Frontend

```bash
# Build production
npm run build

# Lancer les tests
npm test

# Linter et format
npm run lint
npm run format
```

### Database

```bash
# Backup
mysqldump -u root -p erp_dev > backup.sql

# Restore
mysql -u root -p erp_dev < backup.sql

# Voir les tables
mysql -u root -p erp_dev -e "SHOW TABLES;"

# Voir les comptes
mysql -u root -p erp_dev -e "SELECT * FROM accounts;"

# Voir les transactions
mysql -u root -p erp_dev -e "SELECT * FROM transactions;"
```

## ðŸ” TROUBLESHOOTING

### Le backend ne dÃ©marre pas

```bash
# Erreur: MySQL connection refused
# Solution: VÃ©rifier que MySQL est en cours d'exÃ©cution
mysql -u root -p -e "SELECT 1;"

# Erreur: Port 8080 already in use
# Solution: Changer le port dans application.yml
server:
  port: 8081
```

### Le frontend ne peut pas accÃ©der l'API

```bash
# ProblÃ¨me CORS
# Solution: VÃ©rifier que CorsConfig.java est configurÃ©

# Erreur: API_BASE_URL undefined
# Solution: CrÃ©er .env avec REACT_APP_API_URL
echo "REACT_APP_API_URL=http://localhost:8080/api/v1" > frontend/.env
```

### DonnÃ©es initiales non prÃ©sentes

```bash
# VÃ©rifier le seed
mysql -u root -p erp_dev -e "SELECT COUNT(*) as account_count FROM accounts;"

# RÃ©appliquer le seed
mysql -u root -p erp_dev < database/seed.sql
```

## ðŸ“Š STRUCTURE DE DONNÃ‰ES

### Plan Comptable (comptes crÃ©Ã©s automatiquement)

| Code | Compte | Type | Solde Initial |
|------|--------|------|--------------|
| 1010 | Caisse | CASH | 5000â‚¬ |
| 1020 | Banque | ASSET | 50000â‚¬ |
| 1100 | Clients | ASSET | 15000â‚¬ |
| 1200 | Stock | ASSET | 30000â‚¬ |
| 2100 | Fournisseurs | LIABILITY | 10000â‚¬ |
| 2200 | TVA | LIABILITY | 5000â‚¬ |
| 2300 | Emprunts | LIABILITY | 25000â‚¬ |
| 3100 | Capital | EQUITY | 50000â‚¬ |
| 3200 | RÃ©sultats | EQUITY | 15000â‚¬ |
| 4100 | Ventes | REVENUE | 0â‚¬ |
| 5100 | Achats | EXPENSE | 0â‚¬ |
| 5200 | Salaires | EXPENSE | 0â‚¬ |

## ðŸ“ˆ EXEMPLE COMPLET

### ScÃ©nario : VÃ©rifier une Ã©criture comptable

```bash
# Ã‰tape 1 : RÃ©cupÃ©rer l'ID du compte de ventes (4100)
ACCOUNT_ID=$(curl -s http://localhost:8080/api/v1/accounts/type/REVENUE | \
  jq '.data[] | select(.account_number=="4100") | .id')

echo "Account ID: $ACCOUNT_ID"

# Ã‰tape 2 : Ajouter une transaction
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d "{
    \"account_id\": $ACCOUNT_ID,
    \"transaction_type\": \"CREDIT\",
    \"amount\": 5000.00,
    \"reference_number\": \"FAC-2024-001\",
    \"description\": \"Vente marchandises\"
  }"

# Ã‰tape 3 : VÃ©rifier le solde
curl -s http://localhost:8080/api/v1/accounts/$ACCOUNT_ID/balance | \
  jq '.data.balance'
```

## ðŸŽ¯ PROCHAINES Ã‰TAPES

AprÃ¨s le dÃ©marrage rÃ©ussi :

1. âœ… Tester les CRUD (Create, Read, Update, Delete)
2. âœ… IntÃ©grer avec les autres modules
3. âœ… Ajouter l'authentification
4. âœ… ImplÃ©menter la gestion des rÃ´les
5. âœ… Configurer les emails de notification
6. âœ… Mettre en place les sauvegardes

## ðŸ“š DOCUMENTATION COMPLÃˆTE

- [README.md](README.md) - Vue d'ensemble complÃ¨te
- [INTEGRATION.md](INTEGRATION.md) - IntÃ©gration avec les autres modules

## ðŸ’¡ TIPS

- ðŸ’¾ **Snapshot** : Prenez des snapshots de votre base de donnÃ©es avant les tests
- ðŸ“‹ **Logging** : Activez DEBUG dans `application.yml` pour plus de dÃ©tails
- ðŸ”„ **Refresh** : Rechargez la page du navigateur aprÃ¨s les mises Ã  jour API
- ðŸ§¯ **Erreurs** : Consultez les logs du backend pour les erreurs prÃ©cises

---

**Bon dÃ©veloppement! ðŸš€**

