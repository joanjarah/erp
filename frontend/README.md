# Frontend - Interface React

Interface web moderne et responsive pour la gestion comptable.

## Démarrage Rapide

```bash
# Installer les dépendances
npm install

# Développement
npm start

# Build production
npm run build
```

## Pages

### 1. Tableau de Bord (/)
- Vue globale des finances
- Statistiques clés (actifs, passifs, capitaux)
- Liens rapides vers les autres pages

### 2. Gestion des Comptes (/accounts)
- Liste complète des comptes
- Filtres (actifs/tous)
- Créer un nouveau compte
- Voir le solde de chaque compte

### 3. Journal des Transactions (/transactions)
- Toutes les transactions de l'ERP
- Filtrés par compte
- Créer une nouvelle transaction
- Réconciliation

## Structure

```
src/
├── pages/
│   ├── DashboardPage.jsx
│   ├── AccountsPage.jsx
│   └── TransactionsPage.jsx
├── components/
│   ├── AccountList.jsx
│   ├── CreateAccountForm.jsx
│   ├── TransactionList.jsx
│   └── CreateTransactionForm.jsx
├── services/
│   ├── api.js (configuration Axios)
│   ├── accountService.js
│   └── transactionService.js
├── styles/
│   ├── global.css
│   ├── DashboardPage.css
│   ├── AccountsPage.css
│   └── TransactionsPage.css
├── App.js
└── index.js
```

## Configuration

Créer `.env` à la racine du frontend :
```
REACT_APP_API_URL=http://localhost:8080/api/v1
```

## Composants Clés

### AccountList
- Affiche la liste des comptes
- Filtres par statut
- Responsive design

### CreateAccountForm
- Formulaire de création de compte
- Validation des champs
- Sélection du type de compte

### TransactionList
- Historique des transactions
- Formatage des dates
- Affichage du débit/crédit

### CreateTransactionForm
- Formulaire complet de transaction
- Sélection du compte
- Validation des montants

## Système de Design

### Couleurs
- Primaire: #007bff (bleu)
- Succès: #28a745 (vert)
- Danger: #dc3545 (rouge)
- Fond: #f5f5f5

### Composants
- Boutons avec hover effects
- Tables responsive
- Alertes (succès/erreur)
- Cartes statistiques

## API Integration

Tous les appels utilisent le service `accountService` ou `transactionService` :

```javascript
import accountService from '../services/accountService';

// Récupérer les comptes
const comptes = await accountService.getAllAccounts();

// Créer une compte
await accountService.createAccount(data);
```

## State Management

Utilisé `useState` et `useEffect` pour :
- Gestion des listes de données
- Formulaires
- États de loading/erreur

## Performance

- Lazy loading des images (si présentes)
- Mémorisation des composants (React.memo)
- Optimisation des listes (key props)
- Code splitting par route

## Tests

```bash
# Lancer les tests
npm test

# Build production
npm run build

# Analyser la taille du bundle
npm run build -- --analyze
```

## Responsive

Breakpoints :
- Mobile: < 480px
- Tablet: 480px - 768px
- Desktop: > 768px

Toutes les pages sont responsives avec CSS Grid et Flexbox.

## Browser Support

- Chrome (dernière version)
- Firefox (dernière version)
- Safari (dernière version)
- Edge (dernière version)

## Packages

- `react` ^18.2.0 - Framework
- `react-router-dom` ^6.18 - Routing
- `axios` ^1.6.0 - HTTP Client
- `date-fns` ^2.30.0 - Date utilities

## Authentification

Actuellement sans authentification (prêt à intégrer JWT).

Pour ajouter l'auth :
1. Ajouter un intercepteur dans `api.js`
2. Créer une page Login
3. Stocker le token selon le modèle de l'ERP

## Linting

```bash
npm run lint
npm run format
```
