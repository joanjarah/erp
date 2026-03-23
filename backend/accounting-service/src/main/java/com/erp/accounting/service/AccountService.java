package com.erp.accounting.service;

import com.erp.accounting.dto.AccountBalanceDTO;
import com.erp.accounting.dto.AccountDTO;
import com.erp.accounting.dto.StoredProcedureResultDTO;
import com.erp.accounting.entity.Account;
import com.erp.accounting.exception.ResourceNotFoundException;
import com.erp.accounting.exception.ValidationException;
import com.erp.accounting.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private static final Map<String, Account.AccountType> ACCOUNT_TYPE_INPUT_MAP = new HashMap<>();
    private static final Map<String, String> ACCOUNT_TYPE_FR_MAP = new HashMap<>();

    static {
        ACCOUNT_TYPE_INPUT_MAP.put("ASSET", Account.AccountType.ASSET);
        ACCOUNT_TYPE_INPUT_MAP.put("ACTIF", Account.AccountType.ASSET);

        ACCOUNT_TYPE_INPUT_MAP.put("LIABILITY", Account.AccountType.LIABILITY);
        ACCOUNT_TYPE_INPUT_MAP.put("PASSIF", Account.AccountType.LIABILITY);

        ACCOUNT_TYPE_INPUT_MAP.put("EQUITY", Account.AccountType.EQUITY);
        ACCOUNT_TYPE_INPUT_MAP.put("CAPITAUX_PROPRES", Account.AccountType.EQUITY);

        ACCOUNT_TYPE_INPUT_MAP.put("REVENUE", Account.AccountType.REVENUE);
        ACCOUNT_TYPE_INPUT_MAP.put("REVENUS", Account.AccountType.REVENUE);
        ACCOUNT_TYPE_INPUT_MAP.put("PRODUITS", Account.AccountType.REVENUE);

        ACCOUNT_TYPE_INPUT_MAP.put("EXPENSE", Account.AccountType.EXPENSE);
        ACCOUNT_TYPE_INPUT_MAP.put("CHARGES", Account.AccountType.EXPENSE);
        ACCOUNT_TYPE_INPUT_MAP.put("DEPENSES", Account.AccountType.EXPENSE);

        ACCOUNT_TYPE_INPUT_MAP.put("CASH", Account.AccountType.CASH);
        ACCOUNT_TYPE_INPUT_MAP.put("TRESORERIE", Account.AccountType.CASH);
        ACCOUNT_TYPE_INPUT_MAP.put("CAISSE", Account.AccountType.CASH);

        ACCOUNT_TYPE_FR_MAP.put("ASSET", "ACTIF");
        ACCOUNT_TYPE_FR_MAP.put("LIABILITY", "PASSIF");
        ACCOUNT_TYPE_FR_MAP.put("EQUITY", "CAPITAUX_PROPRES");
        ACCOUNT_TYPE_FR_MAP.put("REVENUE", "PRODUITS");
        ACCOUNT_TYPE_FR_MAP.put("EXPENSE", "CHARGES");
        ACCOUNT_TYPE_FR_MAP.put("CASH", "TRESORERIE");
    }

    private final AccountRepository accountRepository;
    private final StoredProcedureService storedProcedureService;

    public AccountService(AccountRepository accountRepository, StoredProcedureService storedProcedureService) {
        this.accountRepository = accountRepository;
        this.storedProcedureService = storedProcedureService;
    }

    /**
     * Creer un nouveau compte.
     */
    public AccountDTO createAccount(AccountDTO accountDTO, String createdBy) {
        log.info("Creation du compte: {}", accountDTO.getAccountNumber());

        if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new ValidationException("Le numero de compte " + accountDTO.getAccountNumber() + " existe deja");
        }

        Account.AccountType normalizedType = normalizeAccountType(accountDTO.getAccountType());

        try {
            StoredProcedureResultDTO result = storedProcedureService.createAccount(
                    accountDTO.getAccountNumber(),
                    accountDTO.getAccountName(),
                    normalizedType.name(),
                    accountDTO.getDescription(),
                    createdBy
            );

            if (!"SUCCESS".equals(result.getStatus())) {
                throw new ValidationException(result.getMessage());
            }

            Long createdId = result.getAccountId();
            if (createdId == null) {
                throw new ValidationException("ID du compte manquant apres creation");
            }

            Account account = accountRepository.findById(createdId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", createdId));

            log.info("Compte cree avec succes: ID={}", createdId);
            return mapToDTO(account);

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la creation du compte: {}", e.getMessage());
            throw new ValidationException("Erreur lors de la creation du compte: " + e.getMessage());
        }
    }

    /**
     * Recuperer tous les comptes (via procedure stockee).
     */
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        log.debug("Recuperation de tous les comptes");
        return localizeAccountTypes(storedProcedureService.listAccounts(null, null));
    }

    /**
     * Recuperer les comptes actifs (via procedure stockee).
     */
    @Transactional(readOnly = true)
    public List<AccountDTO> getActiveAccounts() {
        log.debug("Recuperation des comptes actifs");
        return localizeAccountTypes(storedProcedureService.listAccounts("ACTIVE", null));
    }

    /**
     * Recuperer les comptes par type (via procedure stockee).
     */
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByType(String accountType) {
        log.debug("Recuperation des comptes de type: {}", accountType);
        Account.AccountType normalizedType = normalizeAccountType(accountType);
        return localizeAccountTypes(storedProcedureService.listAccounts(null, normalizedType.name()));
    }

    /**
     * Recuperer un compte par ID.
     */
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long accountId) {
        log.debug("Recuperation du compte: ID={}", accountId);
        if (accountId == null) {
            throw new ValidationException("ID du compte requis");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        return mapToDTO(account);
    }

    /**
     * Recuperer un compte par numero.
     */
    @Transactional(readOnly = true)
    public AccountDTO getAccountByNumber(String accountNumber) {
        log.debug("Recuperation du compte: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compte avec le numero " + accountNumber + " non trouve"));
        return mapToDTO(account);
    }

    /**
     * Obtenir le solde d'un compte via procedure stockee.
     */
    @Transactional(readOnly = false)
    public AccountBalanceDTO getAccountBalance(Long accountId) {
        if (accountId == null) {
            throw new ValidationException("ID du compte requis");
        }

        BigDecimal balance = storedProcedureService.getAccountBalance(accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        AccountBalanceDTO dto = new AccountBalanceDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountName(account.getAccountName());
        dto.setAccountType(toFrenchAccountType(account.getAccountType().name()));
        dto.setBalance(balance);
        dto.setStatus(account.getStatus().toString());
        return dto;
    }

    /**
     * Mettre a jour un compte.
     */
    public AccountDTO updateAccount(Long accountId, AccountDTO accountDTO, String updatedBy) {
        log.info("Mise a jour du compte: ID={}", accountId);
        if (accountId == null) {
            throw new ValidationException("ID du compte requis");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        if (accountDTO.getAccountName() != null) {
            account.setAccountName(accountDTO.getAccountName());
        }
        if (accountDTO.getDescription() != null) {
            account.setDescription(accountDTO.getDescription());
        }
        if (accountDTO.getStatus() != null) {
            try {
                account.setStatus(Account.AccountStatus.valueOf(accountDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Statut invalide: " + accountDTO.getStatus());
            }
        }

        account.setUpdatedBy(updatedBy);
        Account updated = accountRepository.save(account);

        log.info("Compte mis a jour: ID={}", accountId);
        return mapToDTO(updated);
    }

    private List<AccountDTO> localizeAccountTypes(List<AccountDTO> accounts) {
        for (AccountDTO dto : accounts) {
            dto.setAccountType(toFrenchAccountType(dto.getAccountType()));
        }
        return accounts;
    }

    private Account.AccountType normalizeAccountType(String accountType) {
        if (accountType == null || accountType.isBlank()) {
            throw new ValidationException("Type de compte invalide: " + accountType);
        }

        String normalizedKey = accountType.trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');

        Account.AccountType mapped = ACCOUNT_TYPE_INPUT_MAP.get(normalizedKey);
        if (mapped == null) {
            throw new ValidationException("Type de compte invalide: " + accountType);
        }
        return mapped;
    }

    private String toFrenchAccountType(String rawType) {
        if (rawType == null) {
            return null;
        }

        String key = rawType.trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');

        if (ACCOUNT_TYPE_FR_MAP.containsKey(key)) {
            return ACCOUNT_TYPE_FR_MAP.get(key);
        }

        Account.AccountType mapped = ACCOUNT_TYPE_INPUT_MAP.get(key);
        if (mapped != null) {
            return ACCOUNT_TYPE_FR_MAP.get(mapped.name());
        }

        return rawType;
    }

    /**
     * Mapper Account -> AccountDTO.
     */
    private AccountDTO mapToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountName(account.getAccountName());
        dto.setAccountType(toFrenchAccountType(account.getAccountType().name()));
        dto.setDescription(account.getDescription());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus().toString());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        dto.setCreatedBy(account.getCreatedBy());
        dto.setUpdatedBy(account.getUpdatedBy());
        return dto;
    }
}
