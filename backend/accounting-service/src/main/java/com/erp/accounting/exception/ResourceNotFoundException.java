package com.erp.accounting.exception;

public class ResourceNotFoundException extends AccountingException {

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super("RESOURCE_NOT_FOUND", String.format("%s avec l'ID %d non trouvé", resourceName, id));
    }
}
