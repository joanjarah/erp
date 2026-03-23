-- ============================================================================
-- STORED PROCEDURES - ACCOUNTING MODULE
-- ============================================================================

USE erp_dev;
ALTER DATABASE erp_dev
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

DELIMITER $$

DROP PROCEDURE IF EXISTS create_account $$
CREATE PROCEDURE create_account(
    IN p_account_number VARCHAR(50),
    IN p_account_name VARCHAR(255),
    IN p_account_type VARCHAR(50),
    IN p_description TEXT,
    IN p_created_by VARCHAR(100),
    OUT p_account_id BIGINT,
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(500)
)
BEGIN
    DECLARE v_exists INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Erreur SQL lors de la creation du compte';
        SET p_account_id = NULL;
    END;

    create_account_block: BEGIN
        SET p_account_id = NULL;
        SET p_status = 'ERROR';
        SET p_message = '';

        START TRANSACTION;

        IF p_account_number IS NULL OR TRIM(p_account_number) = '' THEN
            SET p_message = 'Le numero de compte est obligatoire';
            ROLLBACK;
            LEAVE create_account_block;
        END IF;

        IF p_account_name IS NULL OR TRIM(p_account_name) = '' THEN
            SET p_message = 'Le nom du compte est obligatoire';
            ROLLBACK;
            LEAVE create_account_block;
        END IF;

        IF p_account_type NOT IN ('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE', 'CASH') THEN
            SET p_message = 'Type de compte invalide';
            ROLLBACK;
            LEAVE create_account_block;
        END IF;

        SELECT COUNT(*) INTO v_exists
        FROM accounts
        WHERE account_number = (p_account_number COLLATE utf8mb4_unicode_ci);

        IF v_exists > 0 THEN
            SET p_message = CONCAT('Le numero de compte ', p_account_number, ' existe deja');
            ROLLBACK;
            LEAVE create_account_block;
        END IF;

        INSERT INTO accounts (
            account_number,
            account_name,
            account_type,
            description,
            balance,
            status,
            created_by,
            updated_by
        ) VALUES (
            p_account_number,
            p_account_name,
            p_account_type,
            p_description,
            0.00,
            'ACTIVE',
            p_created_by,
            p_created_by
        );

        SET p_account_id = LAST_INSERT_ID();

        INSERT INTO chart_of_accounts (
            account_id,
            level,
            is_summary
        ) VALUES (
            p_account_id,
            1,
            FALSE
        );

        COMMIT;
        SET p_status = 'SUCCESS';
        SET p_message = CONCAT('Compte ', p_account_number, ' cree avec succes');
    END create_account_block;
END $$

DROP PROCEDURE IF EXISTS list_accounts $$
CREATE PROCEDURE list_accounts(
    IN p_status VARCHAR(50),
    IN p_account_type VARCHAR(50)
)
BEGIN
    SELECT
        id,
        account_number,
        account_name,
        account_type,
        description,
        balance,
        status,
        created_at,
        updated_at,
        created_by,
        updated_by
    FROM accounts
    WHERE (p_status IS NULL OR status = (p_status COLLATE utf8mb4_unicode_ci))
      AND (p_account_type IS NULL OR account_type = (p_account_type COLLATE utf8mb4_unicode_ci))
    ORDER BY account_number ASC;
END $$

DROP PROCEDURE IF EXISTS get_account_balance $$
CREATE PROCEDURE get_account_balance(
    IN p_account_id BIGINT,
    OUT p_balance DECIMAL(19,2),
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(500)
)
BEGIN
    DECLARE v_exists INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET p_status = 'ERROR';
        SET p_message = 'Erreur SQL lors de la lecture du solde';
        SET p_balance = 0.00;
    END;

    get_account_balance_block: BEGIN
        SET p_balance = 0.00;
        SET p_status = 'ERROR';
        SET p_message = '';

        SELECT COUNT(*) INTO v_exists
        FROM accounts
        WHERE id = p_account_id;

        IF v_exists = 0 THEN
            SET p_message = 'Compte non trouve';
            LEAVE get_account_balance_block;
        END IF;

        SELECT COALESCE(balance, 0.00)
        INTO p_balance
        FROM accounts
        WHERE id = p_account_id;

        SET p_status = 'SUCCESS';
        SET p_message = 'Solde recupere avec succes';
    END get_account_balance_block;
END $$

DROP PROCEDURE IF EXISTS create_transaction $$
CREATE PROCEDURE create_transaction(
    IN p_account_id BIGINT,
    IN p_transaction_type VARCHAR(20),
    IN p_amount DECIMAL(19,2),
    IN p_transaction_date DATE,
    IN p_reference_number VARCHAR(100),
    IN p_description VARCHAR(500),
    IN p_created_by VARCHAR(100),
    OUT p_transaction_id BIGINT,
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(500)
)
BEGIN
    DECLARE v_account_type VARCHAR(50);
    DECLARE v_exists INT DEFAULT 0;
    DECLARE v_date DATE;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Erreur SQL lors de la creation de la transaction';
        SET p_transaction_id = NULL;
    END;

    create_transaction_block: BEGIN
        SET p_transaction_id = NULL;
        SET p_status = 'ERROR';
        SET p_message = '';

        SET v_date = IFNULL(p_transaction_date, CURDATE());

        START TRANSACTION;

        IF p_amount IS NULL OR p_amount <= 0 THEN
            SET p_message = 'Le montant doit etre superieur a 0';
            ROLLBACK;
            LEAVE create_transaction_block;
        END IF;

        IF p_transaction_type NOT IN ('DEBIT', 'CREDIT') THEN
            SET p_message = 'Type de transaction invalide';
            ROLLBACK;
            LEAVE create_transaction_block;
        END IF;

        SELECT COUNT(*) INTO v_exists
        FROM accounts
        WHERE id = p_account_id;

        IF v_exists = 0 THEN
            SET p_message = 'Compte non trouve';
            ROLLBACK;
            LEAVE create_transaction_block;
        END IF;

        SELECT account_type
        INTO v_account_type
        FROM accounts
        WHERE id = p_account_id
        FOR UPDATE;

        INSERT INTO transactions (
            account_id,
            transaction_type,
            amount,
            transaction_date,
            reference_number,
            description,
            status,
            created_by,
            updated_by
        ) VALUES (
            p_account_id,
            p_transaction_type,
            p_amount,
            v_date,
            p_reference_number,
            p_description,
            'POSTED',
            p_created_by,
            p_created_by
        );

        SET p_transaction_id = LAST_INSERT_ID();

        IF v_account_type IN ('ASSET', 'EXPENSE', 'CASH') THEN
            IF p_transaction_type = 'DEBIT' THEN
                UPDATE accounts
                SET balance = balance + p_amount,
                    updated_by = p_created_by
                WHERE id = p_account_id;
            ELSE
                UPDATE accounts
                SET balance = balance - p_amount,
                    updated_by = p_created_by
                WHERE id = p_account_id;
            END IF;
        ELSE
            IF p_transaction_type = 'DEBIT' THEN
                UPDATE accounts
                SET balance = balance - p_amount,
                    updated_by = p_created_by
                WHERE id = p_account_id;
            ELSE
                UPDATE accounts
                SET balance = balance + p_amount,
                    updated_by = p_created_by
                WHERE id = p_account_id;
            END IF;
        END IF;

        COMMIT;
        SET p_status = 'SUCCESS';
        SET p_message = CONCAT('Transaction creee avec ID: ', p_transaction_id);
    END create_transaction_block;
END $$

DROP PROCEDURE IF EXISTS get_transactions $$
CREATE PROCEDURE get_transactions(
    IN p_account_id BIGINT,
    IN p_start_date DATE,
    IN p_end_date DATE,
    IN p_status VARCHAR(50)
)
BEGIN
    SELECT
        t.id,
        t.account_id,
        a.account_number,
        a.account_name,
        t.transaction_date,
        t.transaction_type,
        t.amount,
        t.reference_number,
        t.description,
        t.is_reconciled,
        t.status,
        t.created_at,
        t.created_by
    FROM transactions t
    INNER JOIN accounts a ON t.account_id = a.id
    WHERE (p_account_id IS NULL OR t.account_id = p_account_id)
      AND (p_start_date IS NULL OR t.transaction_date >= p_start_date)
      AND (p_end_date IS NULL OR t.transaction_date <= p_end_date)
      AND (p_status IS NULL OR t.status = (p_status COLLATE utf8mb4_unicode_ci))
    ORDER BY t.transaction_date DESC, t.created_at DESC;
END $$

DROP PROCEDURE IF EXISTS create_journal_entry $$
CREATE PROCEDURE create_journal_entry(
    IN p_entry_number VARCHAR(100),
    IN p_entry_date DATE,
    IN p_description VARCHAR(500),
    IN p_created_by VARCHAR(100),
    OUT p_entry_id BIGINT,
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(500)
)
BEGIN
    DECLARE v_exists INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Erreur SQL lors de la creation de l ecriture';
        SET p_entry_id = NULL;
    END;

    create_journal_entry_block: BEGIN
        SET p_entry_id = NULL;
        SET p_status = 'ERROR';
        SET p_message = '';

        START TRANSACTION;

        IF p_entry_number IS NULL OR TRIM(p_entry_number) = '' THEN
            SET p_message = 'Numero d ecriture obligatoire';
            ROLLBACK;
            LEAVE create_journal_entry_block;
        END IF;

        SELECT COUNT(*) INTO v_exists
        FROM journal_entries
        WHERE entry_number = (p_entry_number COLLATE utf8mb4_unicode_ci);

        IF v_exists > 0 THEN
            SET p_message = CONCAT('Le numero d ecriture ', p_entry_number, ' existe deja');
            ROLLBACK;
            LEAVE create_journal_entry_block;
        END IF;

        INSERT INTO journal_entries (
            entry_number,
            entry_date,
            description,
            status,
            created_by,
            updated_by
        ) VALUES (
            p_entry_number,
            IFNULL(p_entry_date, CURDATE()),
            p_description,
            'DRAFT',
            p_created_by,
            p_created_by
        );

        SET p_entry_id = LAST_INSERT_ID();

        COMMIT;
        SET p_status = 'SUCCESS';
        SET p_message = CONCAT('Ecriture ', p_entry_number, ' creee avec succes');
    END create_journal_entry_block;
END $$

DROP PROCEDURE IF EXISTS add_journal_entry_line $$
CREATE PROCEDURE add_journal_entry_line(
    IN p_entry_id BIGINT,
    IN p_account_id BIGINT,
    IN p_debit_amount DECIMAL(19,2),
    IN p_credit_amount DECIMAL(19,2),
    IN p_line_number INT,
    IN p_description VARCHAR(255),
    IN p_updated_by VARCHAR(100),
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(500)
)
BEGIN
    DECLARE v_total_debit DECIMAL(19,2) DEFAULT 0.00;
    DECLARE v_total_credit DECIMAL(19,2) DEFAULT 0.00;
    DECLARE v_entry_status VARCHAR(50);
    DECLARE v_exists_entry INT DEFAULT 0;
    DECLARE v_exists_account INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Erreur SQL lors de l ajout de la ligne';
    END;

    add_journal_entry_line_block: BEGIN
        SET p_status = 'ERROR';
        SET p_message = '';

        START TRANSACTION;

        IF IFNULL(p_debit_amount, 0) < 0 OR IFNULL(p_credit_amount, 0) < 0 THEN
            SET p_message = 'Les montants ne peuvent pas etre negatifs';
            ROLLBACK;
            LEAVE add_journal_entry_line_block;
        END IF;

        IF IFNULL(p_debit_amount, 0) = 0 AND IFNULL(p_credit_amount, 0) = 0 THEN
            SET p_message = 'Debit ou credit requis';
            ROLLBACK;
            LEAVE add_journal_entry_line_block;
        END IF;

        SELECT COUNT(*) INTO v_exists_entry
        FROM journal_entries
        WHERE id = p_entry_id;

        IF v_exists_entry = 0 THEN
            SET p_message = 'Ecriture comptable non trouvee';
            ROLLBACK;
            LEAVE add_journal_entry_line_block;
        END IF;

        SELECT COUNT(*) INTO v_exists_account
        FROM accounts
        WHERE id = p_account_id;

        IF v_exists_account = 0 THEN
            SET p_message = 'Compte non trouve';
            ROLLBACK;
            LEAVE add_journal_entry_line_block;
        END IF;

        SELECT status INTO v_entry_status
        FROM journal_entries
        WHERE id = p_entry_id
        FOR UPDATE;

        IF v_entry_status <> 'DRAFT' THEN
            SET p_message = 'L ecriture n est pas en edition';
            ROLLBACK;
            LEAVE add_journal_entry_line_block;
        END IF;

        INSERT INTO journal_entry_lines (
            entry_id,
            account_id,
            debit_amount,
            credit_amount,
            line_number,
            description
        ) VALUES (
            p_entry_id,
            p_account_id,
            IFNULL(p_debit_amount, 0),
            IFNULL(p_credit_amount, 0),
            p_line_number,
            p_description
        );

        SELECT
            COALESCE(SUM(debit_amount), 0),
            COALESCE(SUM(credit_amount), 0)
        INTO v_total_debit, v_total_credit
        FROM journal_entry_lines
        WHERE entry_id = p_entry_id;

        UPDATE journal_entries
        SET total_debit = v_total_debit,
            total_credit = v_total_credit,
            is_balanced = (v_total_debit = v_total_credit),
            updated_by = p_updated_by
        WHERE id = p_entry_id;

        COMMIT;
        SET p_status = 'SUCCESS';
        SET p_message = 'Ligne ajoutee avec succes';
    END add_journal_entry_line_block;
END $$

DROP PROCEDURE IF EXISTS post_journal_entry $$
CREATE PROCEDURE post_journal_entry(
    IN p_entry_id BIGINT,
    IN p_updated_by VARCHAR(100),
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(500)
)
BEGIN
    DECLARE v_is_balanced BOOLEAN DEFAULT FALSE;
    DECLARE v_line_count INT DEFAULT 0;
    DECLARE v_exists_entry INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Erreur SQL lors du posting de l ecriture';
    END;

    post_journal_entry_block: BEGIN
        SET p_status = 'ERROR';
        SET p_message = '';

        START TRANSACTION;

        SELECT COUNT(*) INTO v_exists_entry
        FROM journal_entries
        WHERE id = p_entry_id;

        IF v_exists_entry = 0 THEN
            SET p_message = 'Ecriture comptable non trouvee';
            ROLLBACK;
            LEAVE post_journal_entry_block;
        END IF;

        SELECT is_balanced INTO v_is_balanced
        FROM journal_entries
        WHERE id = p_entry_id
        FOR UPDATE;

        IF NOT v_is_balanced THEN
            SET p_message = 'L ecriture n est pas equilibree';
            ROLLBACK;
            LEAVE post_journal_entry_block;
        END IF;

        SELECT COUNT(*) INTO v_line_count
        FROM journal_entry_lines
        WHERE entry_id = p_entry_id;

        IF v_line_count < 2 THEN
            SET p_message = 'L ecriture doit contenir au moins 2 lignes';
            ROLLBACK;
            LEAVE post_journal_entry_block;
        END IF;

        UPDATE accounts a
        INNER JOIN (
            SELECT account_id, debit_amount, credit_amount
            FROM journal_entry_lines
            WHERE entry_id = p_entry_id
        ) jel ON a.id = jel.account_id
        SET a.balance = a.balance + jel.debit_amount - jel.credit_amount,
            a.updated_by = p_updated_by
        WHERE (jel.debit_amount > 0 OR jel.credit_amount > 0);

        UPDATE journal_entries
        SET status = 'POSTED',
            updated_by = p_updated_by
        WHERE id = p_entry_id;

        COMMIT;
        SET p_status = 'SUCCESS';
        SET p_message = 'Ecriture postee avec succes';
    END post_journal_entry_block;
END $$

DROP PROCEDURE IF EXISTS get_account_statement $$
CREATE PROCEDURE get_account_statement(
    IN p_account_id BIGINT,
    IN p_start_date DATE,
    IN p_end_date DATE
)
BEGIN
    SELECT
        t.id,
        t.transaction_date,
        t.transaction_type,
        t.amount,
        t.reference_number,
        t.description,
        CASE WHEN t.transaction_type = 'DEBIT' THEN t.amount ELSE 0 END AS debit,
        CASE WHEN t.transaction_type = 'CREDIT' THEN t.amount ELSE 0 END AS credit,
        (
            SELECT COALESCE(SUM(
                CASE WHEN t2.transaction_type = 'DEBIT' THEN t2.amount ELSE -t2.amount END
            ), 0)
            FROM transactions t2
            WHERE t2.account_id = p_account_id
              AND t2.transaction_date <= t.transaction_date
              AND t2.status = 'POSTED'
        ) AS running_balance,
        t.status
    FROM transactions t
    WHERE t.account_id = p_account_id
      AND (p_start_date IS NULL OR t.transaction_date >= p_start_date)
      AND (p_end_date IS NULL OR t.transaction_date <= p_end_date)
    ORDER BY t.transaction_date ASC;
END $$

DROP PROCEDURE IF EXISTS get_monthly_financial_summary $$
CREATE PROCEDURE get_monthly_financial_summary(
    IN p_year INT,
    IN p_month INT
)
BEGIN
    SELECT
        a.id,
        a.account_number,
        a.account_name,
        a.account_type,
        a.balance AS current_balance,
        COALESCE(SUM(CASE WHEN t.transaction_type = 'DEBIT' THEN t.amount ELSE 0 END), 0) AS month_debits,
        COALESCE(SUM(CASE WHEN t.transaction_type = 'CREDIT' THEN t.amount ELSE 0 END), 0) AS month_credits,
        COALESCE(SUM(CASE WHEN t.transaction_type = 'DEBIT' THEN t.amount ELSE -t.amount END), 0) AS month_net_change
    FROM accounts a
    LEFT JOIN transactions t
           ON a.id = t.account_id
          AND YEAR(t.transaction_date) = p_year
          AND MONTH(t.transaction_date) = p_month
          AND t.status = 'POSTED'
    WHERE a.status = 'ACTIVE'
    GROUP BY a.id, a.account_number, a.account_name, a.account_type, a.balance
    ORDER BY a.account_type, a.account_number;
END $$

DROP PROCEDURE IF EXISTS reconcile_transaction $$
CREATE PROCEDURE reconcile_transaction(
    IN p_transaction_id BIGINT,
    IN p_reconciliation_date DATE,
    OUT p_status VARCHAR(50),
    OUT p_message VARCHAR(500)
)
BEGIN
    DECLARE v_exists INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_status = 'ERROR';
        SET p_message = 'Erreur SQL lors de la reconciliation';
    END;

    reconcile_transaction_block: BEGIN
        SET p_status = 'ERROR';
        SET p_message = '';

        START TRANSACTION;

        SELECT COUNT(*) INTO v_exists
        FROM transactions
        WHERE id = p_transaction_id;

        IF v_exists = 0 THEN
            SET p_message = 'Transaction non trouvee';
            ROLLBACK;
            LEAVE reconcile_transaction_block;
        END IF;

        UPDATE transactions
        SET is_reconciled = TRUE,
            reconciliation_date = IFNULL(p_reconciliation_date, CURDATE())
        WHERE id = p_transaction_id;

        COMMIT;
        SET p_status = 'SUCCESS';
        SET p_message = 'Transaction reconciliee';
    END reconcile_transaction_block;
END $$

DELIMITER ;

