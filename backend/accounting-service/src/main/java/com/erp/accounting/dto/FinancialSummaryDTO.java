package com.erp.accounting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class FinancialSummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("account_type")
    private String accountType;

    @JsonProperty("current_balance")
    private BigDecimal currentBalance;

    @JsonProperty("month_debits")
    private BigDecimal monthDebits;

    @JsonProperty("month_credits")
    private BigDecimal monthCredits;

    @JsonProperty("month_net_change")
    private BigDecimal monthNetChange;

    public FinancialSummaryDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getMonthDebits() {
        return monthDebits;
    }

    public void setMonthDebits(BigDecimal monthDebits) {
        this.monthDebits = monthDebits;
    }

    public BigDecimal getMonthCredits() {
        return monthCredits;
    }

    public void setMonthCredits(BigDecimal monthCredits) {
        this.monthCredits = monthCredits;
    }

    public BigDecimal getMonthNetChange() {
        return monthNetChange;
    }

    public void setMonthNetChange(BigDecimal monthNetChange) {
        this.monthNetChange = monthNetChange;
    }
}
