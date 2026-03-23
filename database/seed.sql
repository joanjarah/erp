-- ============================================================================
-- DONNÃ‰ES INITIALES - PLAN COMPTABLE PAR DÃ‰FAUT
-- ============================================================================

USE erp_dev;

-- Vider les tables existantes
DELETE FROM chart_of_accounts;
DELETE FROM account_history;
DELETE FROM journal_entry_lines;
DELETE FROM journal_entries;
DELETE FROM transactions;
DELETE FROM accounts;

-- ============================================================================
-- ACTIFS (ASSETS) - 1000-1999
-- ============================================================================

-- Caisse (1010)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('1010', 'Caisse', 'CASH', 'Compte de caisse - EspÃ¨ces', 5000.00, 'ACTIVE', 'SYSTEM');

-- Banque (1020)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('1020', 'Banque', 'ASSET', 'Compte bancaire principal', 50000.00, 'ACTIVE', 'SYSTEM');

-- Comptes Clients (1100)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('1100', 'Comptes Clients', 'ASSET', 'CrÃ©ances clients', 15000.00, 'ACTIVE', 'SYSTEM');

-- Stock (1200)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('1200', 'Stock', 'ASSET', 'Inventaire de marchandises', 30000.00, 'ACTIVE', 'SYSTEM');

-- ============================================================================
-- PASSIFS (LIABILITIES) - 2000-2999
-- ============================================================================

-- Comptes Fournisseurs (2100)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('2100', 'Comptes Fournisseurs', 'LIABILITY', 'Dettes fournisseurs', 10000.00, 'ACTIVE', 'SYSTEM');

-- TVA Ã  Payer (2200)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('2200', 'TVA Ã  Payer', 'LIABILITY', 'TVA collectÃ©e Ã  dÃ©caisser', 5000.00, 'ACTIVE', 'SYSTEM');

-- Dettes Bancaires (2300)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('2300', 'Dettes Bancaires', 'LIABILITY', 'Emprunts bancaires', 25000.00, 'ACTIVE', 'SYSTEM');

-- ============================================================================
-- CAPITAUX PROPRES (EQUITY) - 3000-3999
-- ============================================================================

-- Capital Social (3100)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('3100', 'Capital Social', 'EQUITY', 'Capital initial apportÃ©', 50000.00, 'ACTIVE', 'SYSTEM');

-- RÃ©sultats ReportÃ©s (3200)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('3200', 'RÃ©sultats ReportÃ©s', 'EQUITY', 'BÃ©nÃ©fices reportÃ©s', 15000.00, 'ACTIVE', 'SYSTEM');

-- ============================================================================
-- REVENUS (REVENUE) - 4000-4999
-- ============================================================================

-- Ventes (4100)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('4100', 'Ventes de Produits', 'REVENUE', 'Revenus issus de la vente de produits', 0.00, 'ACTIVE', 'SYSTEM');

-- Services (4200)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('4200', 'Revenus de Services', 'REVENUE', 'Revenus issus des services', 0.00, 'ACTIVE', 'SYSTEM');

-- Autres Revenus (4500)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('4500', 'Autres Revenus', 'REVENUE', 'Revenus divers', 0.00, 'ACTIVE', 'SYSTEM');

-- ============================================================================
-- DÃ‰PENSES (EXPENSES) - 5000-5999
-- ============================================================================

-- Achats (5100)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('5100', 'Achats de Marchandises', 'EXPENSE', 'CoÃ»t des marchandises achetÃ©es', 0.00, 'ACTIVE', 'SYSTEM');

-- Salaires (5200)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('5200', 'Salaires et Charges', 'EXPENSE', 'Frais de personnel', 0.00, 'ACTIVE', 'SYSTEM');

-- Loyer (5300)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('5300', 'Loyer', 'EXPENSE', 'Frais de location', 0.00, 'ACTIVE', 'SYSTEM');

-- Ã‰lectricitÃ© (5400)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('5400', 'Ã‰lectricitÃ©', 'EXPENSE', 'Frais Ã©nergÃ©tiques', 0.00, 'ACTIVE', 'SYSTEM');

-- TÃ©lÃ©phone Internet (5500)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('5500', 'TÃ©lÃ©phone et Internet', 'EXPENSE', 'Frais de communication', 0.00, 'ACTIVE', 'SYSTEM');

-- Marketing (5600)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('5600', 'Marketing', 'EXPENSE', 'Frais de marketing et publicitÃ©', 0.00, 'ACTIVE', 'SYSTEM');

-- Maintenance (5700)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('5700', 'Maintenance et RÃ©parations', 'EXPENSE', 'Frais de maintenance', 0.00, 'ACTIVE', 'SYSTEM');

-- Amortissements (5800)
INSERT INTO accounts (account_number, account_name, account_type, description, balance, status, created_by)
VALUES ('5800', 'Amortissements', 'EXPENSE', 'Amortissements des immobilisations', 0.00, 'ACTIVE', 'SYSTEM');

-- ============================================================================
-- POPULATE CHART_OF_ACCOUNTS
-- ============================================================================

INSERT INTO chart_of_accounts (account_id, parent_account_id, level, is_summary)
SELECT id, NULL, 1, FALSE FROM accounts;

-- ============================================================================
-- TRANSACTIONS D'INITIALISATION
-- ============================================================================

-- Transaction : Ouverture du compte de caisse
INSERT INTO transactions (
    account_id,
    transaction_type,
    amount,
    transaction_date,
    reference_number,
    description,
    status,
    created_by
)
VALUES (
    (SELECT id FROM accounts WHERE account_number = '1010'),
    'DEBIT',
    5000.00,
    DATE_SUB(CURDATE(), INTERVAL 10 DAY),
    'INIT-001',
    'Ouverture de caisse',
    'POSTED',
    'SYSTEM'
);

-- Transaction : Ouverture du compte bancaire
INSERT INTO transactions (
    account_id,
    transaction_type,
    amount,
    transaction_date,
    reference_number,
    description,
    status,
    created_by
)
VALUES (
    (SELECT id FROM accounts WHERE account_number = '1020'),
    'DEBIT',
    50000.00,
    DATE_SUB(CURDATE(), INTERVAL 10 DAY),
    'INIT-002',
    'DÃ©pÃ´t initial en banque',
    'POSTED',
    'SYSTEM'
);

-- ============================================================================
-- JOURNAL D'ENTRÃ‰E D'INITIALISATION (Double EntrÃ©e)
-- ============================================================================

-- Ã‰criture d'initialisation du capital
INSERT INTO journal_entries (
    entry_number,
    entry_date,
    description,
    total_debit,
    total_credit,
    is_balanced,
    status,
    created_by
)
VALUES (
    'JE-20260101-001',
    DATE_SUB(CURDATE(), INTERVAL 30 DAY),
    'Ã‰criture d''initialisation - Capital social',
    100000.00,
    100000.00,
    TRUE,
    'POSTED',
    'SYSTEM'
);

SET @entry_id = LAST_INSERT_ID();

-- Ligne 1 : DÃ©bit Caisse
INSERT INTO journal_entry_lines (entry_id, account_id, debit_amount, credit_amount, line_number, description)
VALUES (
    @entry_id,
    (SELECT id FROM accounts WHERE account_number = '1010'),
    5000.00,
    0,
    1,
    'Ouverture caisse'
);

-- Ligne 2 : DÃ©bit Banque
INSERT INTO journal_entry_lines (entry_id, account_id, debit_amount, credit_amount, line_number, description)
VALUES (
    @entry_id,
    (SELECT id FROM accounts WHERE account_number = '1020'),
    50000.00,
    0,
    2,
    'DÃ©pÃ´t initial banque'
);

-- Ligne 3 : DÃ©bit Stock
INSERT INTO journal_entry_lines (entry_id, account_id, debit_amount, credit_amount, line_number, description)
VALUES (
    @entry_id,
    (SELECT id FROM accounts WHERE account_number = '1200'),
    30000.00,
    0,
    3,
    'Stock initial'
);

-- Ligne 4 : CrÃ©dit Capital
INSERT INTO journal_entry_lines (entry_id, account_id, debit_amount, credit_amount, line_number, description)
VALUES (
    @entry_id,
    (SELECT id FROM accounts WHERE account_number = '3100'),
    0,
    50000.00,
    4,
    'Capital versÃ©'
);

-- Ligne 5 : CrÃ©dit RÃ©sultats ReportÃ©s
INSERT INTO journal_entry_lines (entry_id, account_id, debit_amount, credit_amount, line_number, description)
VALUES (
    @entry_id,
    (SELECT id FROM accounts WHERE account_number = '3200'),
    0,
    35000.00,
    5,
    'RÃ©sultats antÃ©rieurs'
);

-- Exemple de transactions mensuelles
-- Vente effectuÃ©e
INSERT INTO transactions (
    account_id,
    transaction_type,
    amount,
    transaction_date,
    reference_number,
    description,
    status,
    created_by
)
VALUES (
    (SELECT id FROM accounts WHERE account_number = '4100'),
    'CREDIT',
    8500.00,
    DATE_SUB(CURDATE(), INTERVAL 5 DAY),
    'FAC-001',
    'Facture de vente #001',
    'POSTED',
    'SYSTEM'
);

-- Achat effectuÃ©
INSERT INTO transactions (
    account_id,
    transaction_type,
    amount,
    transaction_date,
    reference_number,
    description,
    status,
    created_by
)
VALUES (
    (SELECT id FROM accounts WHERE account_number = '5100'),
    'DEBIT',
    3200.00,
    DATE_SUB(CURDATE(), INTERVAL 3 DAY),
    'FACT-F-001',
    'Achat fournisseur #001',
    'POSTED',
    'SYSTEM'
);

-- Paiement salaires
INSERT INTO transactions (
    account_id,
    transaction_type,
    amount,
    transaction_date,
    reference_number,
    description,
    status,
    created_by
)
VALUES (
    (SELECT id FROM accounts WHERE account_number = '5200'),
    'DEBIT',
    12000.00,
    DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    'CHÃˆQUE-001',
    'Paiement salaires du mois',
    'POSTED',
    'SYSTEM'
);

-- ============================================================================
-- HISTORIQUE DES SOLDES
-- ============================================================================

INSERT INTO account_history (account_id, balance_date, opening_balance, closing_balance, total_debits, total_credits)
SELECT
    a.id,
    DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    COALESCE((SELECT SUM(CASE WHEN t.transaction_type = 'DEBIT' THEN -t.amount ELSE t.amount END)
              FROM transactions t
              WHERE t.account_id = a.id
              AND t.transaction_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
              AND t.status = 'POSTED'), 0),
    a.balance,
    COALESCE((SELECT SUM(amount) FROM transactions WHERE account_id = a.id
              AND transaction_type = 'DEBIT'
              AND transaction_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
              AND status = 'POSTED'), 0),
    COALESCE((SELECT SUM(amount) FROM transactions WHERE account_id = a.id
              AND transaction_type = 'CREDIT'
              AND transaction_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
              AND status = 'POSTED'), 0)
FROM accounts a
WHERE a.status = 'ACTIVE';

