package com.erp.accounting.service;

import com.erp.accounting.dto.CreateTransactionDTO;
import com.erp.accounting.dto.StoredProcedureResultDTO;
import com.erp.accounting.dto.TransactionDTO;
import com.erp.accounting.entity.Account;
import com.erp.accounting.entity.Transaction;
import com.erp.accounting.exception.ResourceNotFoundException;
import com.erp.accounting.exception.ValidationException;
import com.erp.accounting.repository.AccountRepository;
import com.erp.accounting.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final StoredProcedureService storedProcedureService;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            StoredProcedureService storedProcedureService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.storedProcedureService = storedProcedureService;
    }

    /**
     * Creer une transaction.
     */
    public TransactionDTO createTransaction(CreateTransactionDTO transactionDTO, String createdBy) {
        log.info("Creation de la transaction pour le compte: {}", transactionDTO.getAccountId());

        if (transactionDTO.getAmount() == null || transactionDTO.getAmount().signum() <= 0) {
            throw new ValidationException("Le montant doit etre positif");
        }

        String transactionType = transactionDTO.getTransactionType();
        if (transactionType == null || transactionType.isBlank()) {
            throw new ValidationException("Le type de transaction est obligatoire");
        }
        transactionType = transactionType.trim().toUpperCase(Locale.ROOT);
        if (!"DEBIT".equals(transactionType) && !"CREDIT".equals(transactionType)) {
            throw new ValidationException("Le type de transaction doit etre DEBIT ou CREDIT");
        }

        Long accountId = transactionDTO.getAccountId();
        if (accountId == null) {
            throw new ValidationException("ID du compte requis");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (!"ACTIVE".equals(account.getStatus().toString())) {
            throw new ValidationException("Le compte n'est pas actif");
        }

        try {
            LocalDate transDate = transactionDTO.getTransactionDate() != null
                    ? transactionDTO.getTransactionDate()
                    : LocalDate.now();

            StoredProcedureResultDTO result = storedProcedureService.createTransaction(
                    accountId,
                    transactionType,
                    transactionDTO.getAmount(),
                    transDate,
                    transactionDTO.getReferenceNumber(),
                    transactionDTO.getDescription(),
                    createdBy
            );

            if (!"SUCCESS".equals(result.getStatus())) {
                throw new ValidationException(result.getMessage());
            }

            Long createdId = result.getTransactionId();
            if (createdId == null) {
                throw new ValidationException("ID de transaction manquant apres creation");
            }

            Transaction transaction = transactionRepository.findById(createdId)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction", createdId));

            log.info("Transaction creee avec succes: ID={}", createdId);
            return mapToDTO(transaction);

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la creation de la transaction: {}", e.getMessage());
            throw new ValidationException("Erreur lors de la creation de la transaction: " + e.getMessage());
        }
    }

    /**
     * Recuperer les transactions d'un compte (via procedure stockee).
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByAccount(Long accountId) {
        log.debug("Recuperation des transactions du compte: {}", accountId);

        if (accountId == null) {
            throw new ValidationException("ID du compte requis");
        }
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account", accountId);
        }

        return storedProcedureService.getTransactions(accountId, null, null, null);
    }

    /**
     * Recuperer les transactions par plage de dates (via procedure stockee).
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Recuperation des transactions de {} a {}", startDate, endDate);

        return storedProcedureService.getTransactions(null, startDate, endDate, null);
    }

    /**
     * Recuperer les transactions d'un compte pour une plage de dates (via procedure stockee).
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByAccountAndDateRange(
            Long accountId, LocalDate startDate, LocalDate endDate) {
        log.debug("Recuperation des transactions du compte {} de {} a {}", accountId, startDate, endDate);

        if (accountId == null) {
            throw new ValidationException("ID du compte requis");
        }
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account", accountId);
        }

        return storedProcedureService.getTransactions(accountId, startDate, endDate, null);
    }

    /**
     * Recuperer toutes les transactions (via procedure stockee).
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        log.debug("Recuperation de toutes les transactions");
        return storedProcedureService.getTransactions(null, null, null, null);
    }

    /**
     * Recuperer les transactions avec filtres (via procedure stockee).
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactions(
            Long accountId,
            LocalDate startDate,
            LocalDate endDate,
            String status) {
        String dbStatus = normalizeStatusForDatabase(status);
        return storedProcedureService.getTransactions(accountId, startDate, endDate, dbStatus);
    }

    /**
     * Recuperer une transaction par ID.
     */
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long transactionId) {
        log.debug("Recuperation de la transaction: ID={}", transactionId);

        if (transactionId == null) {
            throw new ValidationException("ID de transaction requis");
        }
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));

        return mapToDTO(transaction);
    }

    /**
     * Reconcilier une transaction.
     */
    public TransactionDTO reconcileTransaction(Long transactionId, LocalDate reconciliationDate) {
        log.info("Reconciliation de la transaction: ID={}", transactionId);

        if (transactionId == null) {
            throw new ValidationException("ID de transaction requis");
        }
        if (!transactionRepository.existsById(transactionId)) {
            throw new ResourceNotFoundException("Transaction", transactionId);
        }

        try {
            StoredProcedureResultDTO result = storedProcedureService.reconcileTransaction(
                    transactionId,
                    reconciliationDate
            );

            if (!"SUCCESS".equals(result.getStatus())) {
                throw new ValidationException(result.getMessage());
            }

            Transaction refreshed = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));

            log.info("Transaction reconciliee avec succes: ID={}", transactionId);
            return mapToDTO(refreshed);

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la reconciliation: {}", e.getMessage());
            throw new ValidationException("Erreur lors de la reconciliation: " + e.getMessage());
        }
    }

    /**
     * Mapper Transaction -> TransactionDTO.
     */
    private TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAccountId(transaction.getAccount().getId());
        dto.setAccountNumber(transaction.getAccount().getAccountNumber());
        dto.setAccountName(transaction.getAccount().getAccountName());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setTransactionType(transaction.getTransactionType().toString());
        dto.setAmount(transaction.getAmount());
        dto.setReferenceNumber(transaction.getReferenceNumber());
        dto.setDescription(transaction.getDescription());
        dto.setIsReconciled(transaction.getIsReconciled());
        dto.setStatus(toFrenchTransactionStatus(transaction.getStatus().toString()));
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setCreatedBy(transaction.getCreatedBy());
        return dto;
    }

    private String normalizeStatusForDatabase(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        String normalized = status.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "BROUILLON", "DRAFT" -> "DRAFT";
            case "APPROUVEE", "APPROVED" -> "APPROVED";
            case "POSTEE", "POSTE", "POSTED" -> "POSTED";
            case "ANNULEE", "CANCELLED", "CANCELED" -> "CANCELLED";
            default -> throw new ValidationException("Statut de transaction invalide: " + status);
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
