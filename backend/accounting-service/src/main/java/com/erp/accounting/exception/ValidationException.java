package com.erp.accounting.exception;

public class ValidationException extends AccountingException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}
