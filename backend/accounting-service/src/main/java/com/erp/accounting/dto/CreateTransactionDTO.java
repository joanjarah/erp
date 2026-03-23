package com.erp.accounting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTransactionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "L'ID du compte est obligatoire")
    @JsonProperty("account_id")
    @JsonAlias({"accountId"})
    private Long accountId;

    @NotNull(message = "Le type de transaction est obligatoire")
    @JsonProperty("transaction_type")
    @JsonAlias({"transactionType", "type"})
    private String transactionType; // DEBIT ou CREDIT

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount;

    @JsonProperty("transaction_date")
    @JsonAlias({"transactionDate"})
    private LocalDate transactionDate;

    @JsonProperty("reference_number")
    @JsonAlias({"referenceNumber"})
    private String referenceNumber;

    private String description;

    public CreateTransactionDTO() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
