package com.erp.accounting.exception;

public class AccountingException extends RuntimeException {
    
    private final String errorCode;

    public AccountingException(String message) {
        super(message);
        this.errorCode = "ACCOUNTING_ERROR";
    }

    public AccountingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "ACCOUNTING_ERROR";
    }

    public AccountingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AccountingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
