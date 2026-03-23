package com.erp.accounting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class StoredProcedureResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("transaction_id")
    private Long transactionId;

    @JsonProperty("entry_id")
    private Long entryId;

    public StoredProcedureResultDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }
}
