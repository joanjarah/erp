package com.erp.accounting.controller;

import com.erp.accounting.dto.ApiResponseDTO;
import com.erp.accounting.dto.CreateTransactionDTO;
import com.erp.accounting.dto.TransactionDTO;
import com.erp.accounting.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/transactions", "/api/transactions", "/api/v1/transactions"})
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
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * POST /transactions - Créer une transaction
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<TransactionDTO>> createTransaction(
            @Valid @RequestBody CreateTransactionDTO transactionDTO,
            Principal principal) {
        log.info("POST /transactions - Création de la transaction");

        String createdBy = principal != null ? principal.getName() : "SYSTEM";
        TransactionDTO created = transactionService.createTransaction(transactionDTO, createdBy);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(created, "Transaction créée avec succès"));
    }

    /**
     * GET /transactions - Récupérer toutes les transactions (filtres optionnels)
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TransactionDTO>>> getAllTransactions(
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status) {
        log.info("GET /transactions - Récupération des transactions");

        List<TransactionDTO> transactions = transactionService.getTransactions(accountId, startDate, endDate, status);

        return ResponseEntity.ok(
                ApiResponseDTO.success(transactions, "Transactions récupérées avec succès"));
    }

    /**
     * GET /transactions/{id} - Récupérer une transaction par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TransactionDTO>> getTransactionById(
            @PathVariable Long id) {
        log.info("GET /transactions/{} - Récupération de la transaction", id);

        TransactionDTO transaction = transactionService.getTransactionById(id);

        return ResponseEntity.ok(
                ApiResponseDTO.success(transaction, "Transaction récupérée avec succès"));
    }

    /**
     * GET /transactions/account/{accountId} - Récupérer les transactions d'un compte
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponseDTO<List<TransactionDTO>>> getTransactionsByAccount(
            @PathVariable Long accountId) {
        log.info("GET /transactions/account/{} - Récupération des transactions", accountId);

        List<TransactionDTO> transactions = transactionService.getTransactionsByAccount(accountId);

        return ResponseEntity.ok(
                ApiResponseDTO.success(transactions, "Transactions du compte récupérées"));
    }

    /**
     * GET /transactions/account/{accountId}/range - Récupérer les transactions par plage de dates
     */
    @GetMapping("/account/{accountId}/range")
    public ResponseEntity<ApiResponseDTO<List<TransactionDTO>>> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /transactions/account/{}/range - Récupération par plage de dates", accountId);

        List<TransactionDTO> transactions = transactionService
                .getTransactionsByAccountAndDateRange(accountId, startDate, endDate);

        return ResponseEntity.ok(
                ApiResponseDTO.success(transactions, "Transactions récupérées"));
    }

    /**
     * PATCH /transactions/{id}/reconcile - Réconcilier une transaction
     */
    @PatchMapping("/{id}/reconcile")
    public ResponseEntity<ApiResponseDTO<TransactionDTO>> reconcileTransaction(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reconciliationDate) {
        log.info("PATCH /transactions/{}/reconcile - Réconciliation", id);

        LocalDate date = reconciliationDate != null ? reconciliationDate : LocalDate.now();
        TransactionDTO transaction = transactionService.reconcileTransaction(id, date);

        return ResponseEntity.ok(
                ApiResponseDTO.success(transaction, "Transaction réconciliée avec succès"));
    }
}
