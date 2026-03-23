package com.erp.accounting.service;

import com.erp.accounting.dto.AccountDTO;
import com.erp.accounting.dto.FinancialSummaryDTO;
import com.erp.accounting.dto.StoredProcedureResultDTO;
import com.erp.accounting.dto.TransactionDTO;
import com.erp.accounting.exception.AccountingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class StoredProcedureService {

    private static final Logger log = LoggerFactory.getLogger(StoredProcedureService.class);

    private final JdbcTemplate jdbcTemplate;

    public StoredProcedureService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AccountDTO> listAccounts(String status, String accountType) {
        String sql = "CALL list_accounts(?, ?)";
        return jdbcTemplate.query(sql, accountRowMapper(), status, accountType);
    }

    public List<TransactionDTO> getTransactions(
            Long accountId,
            LocalDate startDate,
            LocalDate endDate,
            String status) {
        String sql = "CALL get_transactions(?, ?, ?, ?)";
        Date start = startDate != null ? Date.valueOf(startDate) : null;
        Date end = endDate != null ? Date.valueOf(endDate) : null;
        return jdbcTemplate.query(sql, transactionRowMapper(), accountId, start, end, status);
    }

    public List<FinancialSummaryDTO> getMonthlyFinancialSummary(int year, int month) {
        String sql = "CALL get_monthly_financial_summary(?, ?)";
        return jdbcTemplate.query(sql, financialSummaryRowMapper(), year, month);
    }

    /**
     * Cree un compte via la procedure CREATE_ACCOUNT.
     */
    public StoredProcedureResultDTO createAccount(
            String accountNumber,
            String accountName,
            String accountType,
            String description,
            String createdBy) {

        try {
            StoredProcedureResultDTO dto = jdbcTemplate.execute(
                    "{call create_account(?, ?, ?, ?, ?, ?, ?, ?)}",
                    (CallableStatement cs) -> {
                        cs.setString(1, accountNumber);
                        cs.setString(2, accountName);
                        cs.setString(3, accountType);
                        cs.setString(4, description);
                        cs.setString(5, createdBy);
                        cs.registerOutParameter(6, Types.BIGINT);
                        cs.registerOutParameter(7, Types.VARCHAR);
                        cs.registerOutParameter(8, Types.VARCHAR);
                        cs.execute();

                        StoredProcedureResultDTO result = new StoredProcedureResultDTO();
                        long accountId = cs.getLong(6);
                        if (!cs.wasNull()) {
                            result.setAccountId(accountId);
                        }
                        result.setStatus(cs.getString(7));
                        result.setMessage(cs.getString(8));
                        return result;
                    });

            if (dto == null) {
                throw new AccountingException("PROCEDURE_ERROR", "Aucune reponse de la procedure create_account");
            }
            return dto;

        } catch (Exception e) {
            log.error("Erreur lors de la creation du compte: {}", e.getMessage(), e);
            throw new AccountingException("PROCEDURE_ERROR", "Erreur lors de la creation du compte: " + e.getMessage());
        }
    }

    /**
     * Cree une transaction via la procedure CREATE_TRANSACTION.
     */
    public StoredProcedureResultDTO createTransaction(
            Long accountId,
            String transactionType,
            BigDecimal amount,
            LocalDate transactionDate,
            String referenceNumber,
            String description,
            String createdBy) {

        try {
            StoredProcedureResultDTO dto = jdbcTemplate.execute(
                    "{call create_transaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}",
                    (CallableStatement cs) -> {
                        cs.setLong(1, accountId);
                        cs.setString(2, transactionType);
                        cs.setBigDecimal(3, amount);
                        cs.setDate(4, Date.valueOf(transactionDate));
                        cs.setString(5, referenceNumber);
                        cs.setString(6, description);
                        cs.setString(7, createdBy);
                        cs.registerOutParameter(8, Types.BIGINT);
                        cs.registerOutParameter(9, Types.VARCHAR);
                        cs.registerOutParameter(10, Types.VARCHAR);
                        cs.execute();

                        StoredProcedureResultDTO result = new StoredProcedureResultDTO();
                        long transactionId = cs.getLong(8);
                        if (!cs.wasNull()) {
                            result.setTransactionId(transactionId);
                        }
                        result.setStatus(cs.getString(9));
                        result.setMessage(cs.getString(10));
                        return result;
                    });

            if (dto == null) {
                throw new AccountingException("PROCEDURE_ERROR", "Aucune reponse de la procedure create_transaction");
            }
            return dto;

        } catch (Exception e) {
            log.error("Erreur lors de la creation de la transaction: {}", e.getMessage(), e);
            throw new AccountingException("PROCEDURE_ERROR", "Erreur lors de la creation de la transaction: " + e.getMessage());
        }
    }

    /**
     * Recupere le solde d'un compte via GET_ACCOUNT_BALANCE.
     */
    public BigDecimal getAccountBalance(Long accountId) {

        try {
            BigDecimal balance = jdbcTemplate.execute(
                    "{call get_account_balance(?, ?, ?, ?)}",
                    (CallableStatement cs) -> {
                        cs.setLong(1, accountId);
                        cs.registerOutParameter(2, Types.DECIMAL);
                        cs.registerOutParameter(3, Types.VARCHAR);
                        cs.registerOutParameter(4, Types.VARCHAR);
                        cs.execute();

                        String status = cs.getString(3);
                        String message = cs.getString(4);
                        if ("ERROR".equals(status)) {
                            throw new AccountingException("PROCEDURE_ERROR", message);
                        }
                        return cs.getBigDecimal(2);
                    });

            return balance != null ? balance : BigDecimal.ZERO;

        } catch (Exception e) {
            log.error("Erreur lors de la recuperation du solde: {}", e.getMessage(), e);
            throw new AccountingException("PROCEDURE_ERROR", "Erreur lors de la recuperation du solde: " + e.getMessage());
        }
    }

    /**
     * Reconcilie une transaction.
     */
    public StoredProcedureResultDTO reconcileTransaction(
            Long transactionId,
            LocalDate reconciliationDate) {

        try {
            StoredProcedureResultDTO dto = jdbcTemplate.execute(
                    "{call reconcile_transaction(?, ?, ?, ?)}",
                    (CallableStatement cs) -> {
                        cs.setLong(1, transactionId);
                        cs.setDate(2, Date.valueOf(reconciliationDate));
                        cs.registerOutParameter(3, Types.VARCHAR);
                        cs.registerOutParameter(4, Types.VARCHAR);
                        cs.execute();

                        StoredProcedureResultDTO result = new StoredProcedureResultDTO();
                        result.setStatus(cs.getString(3));
                        result.setMessage(cs.getString(4));
                        return result;
                    });

            if (dto == null) {
                throw new AccountingException("PROCEDURE_ERROR", "Aucune reponse de la procedure reconcile_transaction");
            }
            return dto;

        } catch (Exception e) {
            log.error("Erreur lors de la reconciliation: {}", e.getMessage(), e);
            throw new AccountingException("PROCEDURE_ERROR", "Erreur lors de la reconciliation: " + e.getMessage());
        }
    }

    /**
     * Cree une ecriture comptable.
     */
    public StoredProcedureResultDTO createJournalEntry(
            String entryNumber,
            LocalDate entryDate,
            String description,
            String createdBy) {

        try {
            StoredProcedureResultDTO dto = jdbcTemplate.execute(
                    "{call create_journal_entry(?, ?, ?, ?, ?, ?, ?)}",
                    (CallableStatement cs) -> {
                        cs.setString(1, entryNumber);
                        cs.setDate(2, Date.valueOf(entryDate));
                        cs.setString(3, description);
                        cs.setString(4, createdBy);
                        cs.registerOutParameter(5, Types.BIGINT);
                        cs.registerOutParameter(6, Types.VARCHAR);
                        cs.registerOutParameter(7, Types.VARCHAR);
                        cs.execute();

                        StoredProcedureResultDTO result = new StoredProcedureResultDTO();
                        long entryId = cs.getLong(5);
                        if (!cs.wasNull()) {
                            result.setEntryId(entryId);
                        }
                        result.setStatus(cs.getString(6));
                        result.setMessage(cs.getString(7));
                        return result;
                    });

            if (dto == null) {
                throw new AccountingException("PROCEDURE_ERROR", "Aucune reponse de la procedure create_journal_entry");
            }
            return dto;

        } catch (Exception e) {
            log.error("Erreur lors de la creation de l'ecriture: {}", e.getMessage(), e);
            throw new AccountingException("PROCEDURE_ERROR", "Erreur lors de la creation de l'ecriture: " + e.getMessage());
        }
    }

    private RowMapper<AccountDTO> accountRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            AccountDTO dto = new AccountDTO();
            long id = rs.getLong("id");
            dto.setId(rs.wasNull() ? null : id);
            dto.setAccountNumber(rs.getString("account_number"));
            dto.setAccountName(rs.getString("account_name"));
            dto.setAccountType(rs.getString("account_type"));
            dto.setDescription(rs.getString("description"));
            dto.setBalance(rs.getBigDecimal("balance"));
            dto.setStatus(rs.getString("status"));
            Timestamp createdAt = rs.getTimestamp("created_at");
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            dto.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
            dto.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
            dto.setCreatedBy(rs.getString("created_by"));
            dto.setUpdatedBy(rs.getString("updated_by"));
            return dto;
        };
    }

    private RowMapper<TransactionDTO> transactionRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            TransactionDTO dto = new TransactionDTO();
            long id = rs.getLong("id");
            dto.setId(rs.wasNull() ? null : id);
            long accountId = rs.getLong("account_id");
            dto.setAccountId(rs.wasNull() ? null : accountId);
            dto.setAccountNumber(rs.getString("account_number"));
            dto.setAccountName(rs.getString("account_name"));
            Date transactionDate = rs.getDate("transaction_date");
            dto.setTransactionDate(transactionDate != null ? transactionDate.toLocalDate() : null);
            dto.setTransactionType(rs.getString("transaction_type"));
            dto.setAmount(rs.getBigDecimal("amount"));
            dto.setReferenceNumber(rs.getString("reference_number"));
            dto.setDescription(rs.getString("description"));
            Object reconciledObj = rs.getObject("is_reconciled");
            dto.setIsReconciled(reconciledObj != null ? rs.getBoolean("is_reconciled") : null);
            dto.setStatus(toFrenchTransactionStatus(rs.getString("status")));
            Timestamp createdAt = rs.getTimestamp("created_at");
            dto.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
            dto.setCreatedBy(rs.getString("created_by"));
            return dto;
        };
    }

    private RowMapper<FinancialSummaryDTO> financialSummaryRowMapper() {
        return (ResultSet rs, int rowNum) -> {
            FinancialSummaryDTO dto = new FinancialSummaryDTO();
            long id = rs.getLong("id");
            dto.setId(rs.wasNull() ? null : id);
            dto.setAccountNumber(rs.getString("account_number"));
            dto.setAccountName(rs.getString("account_name"));
            dto.setAccountType(rs.getString("account_type"));
            dto.setCurrentBalance(rs.getBigDecimal("current_balance"));
            dto.setMonthDebits(rs.getBigDecimal("month_debits"));
            dto.setMonthCredits(rs.getBigDecimal("month_credits"));
            dto.setMonthNetChange(rs.getBigDecimal("month_net_change"));
            return dto;
        };
    }

    private String toFrenchTransactionStatus(String status) {
        if (status == null) {
            return null;
        }

        String normalized = status.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "DRAFT" -> "BROUILLON";
            case "APPROVED" -> "APPROUVEE";
            case "POSTED" -> "POSTEE";
            case "CANCELLED", "CANCELED" -> "ANNULEE";
            default -> status;
        };
    }
}

