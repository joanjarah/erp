package com.erp.accounting.controller;

import com.erp.accounting.dto.AccountBalanceDTO;
import com.erp.accounting.dto.AccountDTO;
import com.erp.accounting.dto.ApiResponseDTO;
import com.erp.accounting.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/accounts", "/api/accounts", "/api/v1/accounts"})
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:3001",
                "http://127.0.0.1:3001",
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:4173",
                "http://127.0.0.1:4173"
        },
        allowCredentials = "true"
)
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * POST /accounts - Créer un compte
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<AccountDTO>> createAccount(
            @Valid @RequestBody AccountDTO accountDTO,
            Principal principal) {
        log.info("POST /accounts - Création du compte: {}", accountDTO.getAccountNumber());

        String createdBy = principal != null ? principal.getName() : "SYSTEM";
        AccountDTO created = accountService.createAccount(accountDTO, createdBy);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(created, "Compte créé avec succès"));
    }

    /**
     * GET /accounts - Récupérer tous les comptes
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AccountDTO>>> getAllAccounts() {
        log.info("GET /accounts - Récupération de tous les comptes");

        List<AccountDTO> accounts = accountService.getAllAccounts();

        return ResponseEntity.ok(
                ApiResponseDTO.success(accounts, "Comptes récupérés avec succès"));
    }

    /**
     * GET /accounts/active - Récupérer les comptes actifs
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<AccountDTO>>> getActiveAccounts() {
        log.info("GET /accounts/active - Récupération des comptes actifs");

        List<AccountDTO> accounts = accountService.getActiveAccounts();

        return ResponseEntity.ok(
                ApiResponseDTO.success(accounts, "Comptes actifs récupérés avec succès"));
    }

    /**
     * GET /accounts/type/{type} - Récupérer les comptes par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponseDTO<List<AccountDTO>>> getAccountsByType(
            @PathVariable String type) {
        log.info("GET /accounts/type/{} - Récupération des comptes de type: {}", type, type);

        List<AccountDTO> accounts = accountService.getAccountsByType(type);

        return ResponseEntity.ok(
                ApiResponseDTO.success(accounts, "Comptes de type " + type + " récupérés"));
    }

    /**
     * GET /accounts/number/{number} - Récupérer un compte par numéro (route SPÉCIFIQUE en premier)
     */
    @GetMapping("/number/{number}")
    public ResponseEntity<ApiResponseDTO<AccountDTO>> getAccountByNumber(
            @PathVariable String number) {
        log.info("GET /accounts/number/{} - Récupération du compte", number);

        AccountDTO account = accountService.getAccountByNumber(number);

        return ResponseEntity.ok(
                ApiResponseDTO.success(account, "Compte récupéré avec succès"));
    }

    /**
     * GET /accounts/{id}/balance - Récupérer le solde d'un compte (route SPÉCIFIQUE en premier)
     */
    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiResponseDTO<AccountBalanceDTO>> getAccountBalance(
            @PathVariable Long id) {
        log.info("GET /accounts/{}/balance - Récupération du solde", id);

        AccountBalanceDTO balance = accountService.getAccountBalance(id);

        return ResponseEntity.ok(
                ApiResponseDTO.success(balance, "Solde récupéré avec succès"));
    }

    /**
     * GET /accounts/{id} - Récupérer un compte par ID (route GÉNÉRIQUE à la fin)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<AccountDTO>> getAccountById(
            @PathVariable Long id) {
        log.info("GET /accounts/{} - Récupération du compte", id);

        AccountDTO account = accountService.getAccountById(id);

        return ResponseEntity.ok(
                ApiResponseDTO.success(account, "Compte récupéré avec succès"));
    }

    /**
     * PUT /accounts/{id} - Mettre à jour un compte
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<AccountDTO>> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountDTO accountDTO,
            Principal principal) {
        log.info("PUT /accounts/{} - Mise à jour du compte", id);

        String updatedBy = principal != null ? principal.getName() : "SYSTEM";
        AccountDTO updated = accountService.updateAccount(id, accountDTO, updatedBy);

        return ResponseEntity.ok(
                ApiResponseDTO.success(updated, "Compte mis à jour avec succès"));
    }
}
