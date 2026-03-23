-- ============================================================================
-- SCHEMA COMPTABILITÃ‰ - ERP STARTUP
-- ============================================================================

CREATE DATABASE IF NOT EXISTS erp_dev
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE erp_dev;
ALTER DATABASE erp_dev
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: ACCOUNTS (Comptes)
-- ============================================================================
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    account_name VARCHAR(255) NOT NULL,
    account_type ENUM('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE', 'CASH') NOT NULL,
    description TEXT,
    balance DECIMAL(19, 2) DEFAULT 0.00,
    status ENUM('ACTIVE', 'INACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT accounts_uk UNIQUE (account_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indices
CREATE INDEX idx_account_type ON accounts(account_type);
CREATE INDEX idx_account_status ON accounts(status);
CREATE INDEX idx_account_number ON accounts(account_number);

-- ============================================================================
-- TABLE: TRANSACTIONS (Transactions)
-- ============================================================================
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_date DATE NOT NULL,
    transaction_type ENUM('DEBIT', 'CREDIT') NOT NULL,
    account_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    reference_number VARCHAR(100),
    description VARCHAR(500),
    is_reconciled BOOLEAN DEFAULT FALSE,
    reconciliation_date DATE NULL,
    status ENUM('DRAFT', 'APPROVED', 'POSTED', 'CANCELLED') DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) 
        REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT transactions_amount_positive CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indices
CREATE INDEX idx_transaction_date ON transactions(transaction_date);
CREATE INDEX idx_transaction_account ON transactions(account_id);
CREATE INDEX idx_transaction_status ON transactions(status);
CREATE INDEX idx_transaction_reconciled ON transactions(is_reconciled);

-- ============================================================================
-- TABLE: JOURNAL_ENTRIES (Ã‰critures Comptables - Double Entry)
-- ============================================================================
CREATE TABLE IF NOT EXISTS journal_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entry_date DATE NOT NULL,
    entry_number VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    total_debit DECIMAL(19, 2) DEFAULT 0.00,
    total_credit DECIMAL(19, 2) DEFAULT 0.00,
    is_balanced BOOLEAN DEFAULT FALSE,
    status ENUM('DRAFT', 'POSTED', 'CANCELLED') DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT journal_entries_check CHECK (total_debit = total_credit OR is_balanced = FALSE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indices
CREATE INDEX idx_journal_entry_date ON journal_entries(entry_date);
CREATE INDEX idx_journal_entry_status ON journal_entries(status);
CREATE INDEX idx_journal_entry_number ON journal_entries(entry_number);

-- ============================================================================
-- TABLE: JOURNAL_ENTRY_LINES (Lignes des Ã©critures comptables)
-- ============================================================================
CREATE TABLE IF NOT EXISTS journal_entry_lines (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entry_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    debit_amount DECIMAL(19, 2) DEFAULT 0.00,
    credit_amount DECIMAL(19, 2) DEFAULT 0.00,
    line_number INT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_journal_line_entry FOREIGN KEY (entry_id) 
        REFERENCES journal_entries(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_journal_line_account FOREIGN KEY (account_id) 
        REFERENCES accounts(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT journal_line_amounts CHECK (debit_amount >= 0 AND credit_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indices
CREATE INDEX idx_journal_line_entry ON journal_entry_lines(entry_id);
CREATE INDEX idx_journal_line_account ON journal_entry_lines(account_id);

-- ============================================================================
-- TABLE: ACCOUNT_HISTORY (Historique des soldes)
-- ============================================================================
CREATE TABLE IF NOT EXISTS account_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    balance_date DATE NOT NULL,
    opening_balance DECIMAL(19, 2),
    closing_balance DECIMAL(19, 2),
    total_debits DECIMAL(19, 2),
    total_credits DECIMAL(19, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_account FOREIGN KEY (account_id) 
        REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uk_account_history UNIQUE (account_id, balance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indices
CREATE INDEX idx_history_date ON account_history(balance_date);
CREATE INDEX idx_history_account ON account_history(account_id);

-- ============================================================================
-- TABLE: CHART_OF_ACCOUNTS_MAPPING (Mapping des plans comptables)
-- ============================================================================
CREATE TABLE IF NOT EXISTS chart_of_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL UNIQUE,
    parent_account_id BIGINT NULL,
    level INT,
    is_summary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_coa_account FOREIGN KEY (account_id) 
        REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_coa_parent FOREIGN KEY (parent_account_id) 
        REFERENCES chart_of_accounts(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indices
CREATE INDEX idx_coa_parent ON chart_of_accounts(parent_account_id);
CREATE INDEX idx_coa_level ON chart_of_accounts(level);

