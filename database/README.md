# Base de DonnÃ©es

Contient tous les fichiers SQL pour initialiser et configurer la base de donnÃ©es MySQL.

## Fichiers

- `schema.sql` - CrÃ©ation complÃ¨te de la structure (tables, indices, contraintes)
- `procedures.sql` - 11 procÃ©dures stockÃ©es robustes
- `seed.sql` - DonnÃ©es initiales (plan comptable par dÃ©faut et transactions d'exemple)

## Utilisation

```bash
# CrÃ©er la base de donnÃ©es
mysql -u root -p < schema.sql

# Ajouter les procÃ©dures
mysql -u root -p erp_dev < procedures.sql

# Charger les donnÃ©es
mysql -u root -p erp_dev < seed.sql
```

## Plan Comptable Initial

22 comptes prÃ©-configurÃ©s :
- 4 comptes ACTIFS (Caisse, Banque, Clients, Stock)
- 3 comptes PASSIFS (Fournisseurs, TVA, Dettes Bancaires)
- 2 comptes CAPITAUX PROPRES
- 3 comptes REVENUS
- 10 comptes DÃ‰PENSES

Solde initial total : 100 000â‚¬

