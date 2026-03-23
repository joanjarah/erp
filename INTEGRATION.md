# Integration Inter-Modules - Comptabilite

## Contraintes respectees

1. Le module comptabilite n'accede a aucune table des modules Ventes, Achats ou RH.
2. Les flux entrants passent uniquement par API REST.
3. Endpoint principal expose: `POST /api/transactions`.

## DTO principal (entrant)

```json
{
  "accountId": 10,
  "amount": 1500.00,
  "type": "CREDIT",
  "description": "Vente facture INV-2026-0045"
}
```

Notes:
- `type`: `DEBIT` ou `CREDIT`
- `transactionDate` et `referenceNumber` restent optionnels (geres par le backend)

## Backend Comptabilite

Le endpoint est gere dans `backend/accounting-service/src/main/java/com/erp/accounting/controller/TransactionController.java`.

- Routes exposees (compatibilite):
  - `POST /api/transactions`
  - `POST /transactions`
  - `POST /api/v1/transactions`

Le traitement met a jour les soldes via procedure stockee:
- Service: `backend/accounting-service/src/main/java/com/erp/accounting/service/TransactionService.java`
- Procedure caller: `backend/accounting-service/src/main/java/com/erp/accounting/service/StoredProcedureService.java`
- Procedure SQL: `database/procedures.sql` -> `create_transaction`

## Exemple Java - Module Ventes

```java
package com.erp.sales.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class AccountingClient {

    private final RestClient restClient;

    public AccountingClient(@Value("${accounting.api.base-url}") String accountingBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(accountingBaseUrl) // ex: http://localhost:8080
                .build();
    }

    public void postSaleRevenue(Long revenueAccountId, BigDecimal amount, String invoiceNumber) {
        TransactionRequestDto payload = new TransactionRequestDto(
                revenueAccountId,
                amount,
                "CREDIT",
                "Vente facture " + invoiceNumber
        );

        restClient.post()
                .uri("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    public record TransactionRequestDto(
            Long accountId,
            BigDecimal amount,
            String type,
            String description
    ) {}
}
```

## Exemple Java - Module Achats

```java
package com.erp.purchases.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class AccountingClient {

    private final RestClient restClient;

    public AccountingClient(@Value("${accounting.api.base-url}") String accountingBaseUrl) {
        this.restClient = RestClient.builder().baseUrl(accountingBaseUrl).build();
    }

    public void postPurchaseExpense(Long expenseAccountId, BigDecimal amount, String supplierInvoice) {
        TransactionRequestDto payload = new TransactionRequestDto(
                expenseAccountId,
                amount,
                "DEBIT",
                "Achat fournisseur " + supplierInvoice
        );

        restClient.post()
                .uri("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    public record TransactionRequestDto(
            Long accountId,
            BigDecimal amount,
            String type,
            String description
    ) {}
}
```

## Exemple Java - Module RH

```java
package com.erp.hr.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class AccountingClient {

    private final RestClient restClient;

    public AccountingClient(@Value("${accounting.api.base-url}") String accountingBaseUrl) {
        this.restClient = RestClient.builder().baseUrl(accountingBaseUrl).build();
    }

    public void postPayroll(Long salaryExpenseAccountId, BigDecimal payrollAmount, String payrollRef) {
        TransactionRequestDto payload = new TransactionRequestDto(
                salaryExpenseAccountId,
                payrollAmount,
                "DEBIT",
                "Paie " + payrollRef
        );

        restClient.post()
                .uri("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    public record TransactionRequestDto(
            Long accountId,
            BigDecimal amount,
            String type,
            String description
    ) {}
}
```

## Logique d'integration (concise)

1. Ventes/Achats/RH finalisent leur operation metier dans leur propre base.
2. Chaque module appelle `POST /api/transactions` du module comptabilite avec le DTO minimal.
3. Le module comptabilite valide, appelle la procedure `create_transaction`, puis met a jour le solde du compte.
4. Aucune jointure ni lecture des tables externes n'est faite par la comptabilite.
