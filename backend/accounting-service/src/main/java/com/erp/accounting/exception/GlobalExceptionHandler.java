package com.erp.accounting.exception;

import com.erp.accounting.dto.ApiResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Gère les exceptions de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        log.warn("Erreur de validation: {}", ex.getMessage());

        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(errors, request.getDescription(false)));
    }

    /**
     * Gère les exceptions de validation métier
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleValidationBusinessException(
            ValidationException ex,
            WebRequest request) {
        log.warn("Erreur de validation métier: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(ex.getMessage(), request.getDescription(false)));
    }

    /**
     * Gère les exceptions de ressource non trouvée
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        log.warn("Ressource non trouvée: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.error(ex.getMessage(), request.getDescription(false)));
    }

    /**
     * Gère les exceptions générales
     */
    @ExceptionHandler(AccountingException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleAccountingException(
            AccountingException ex,
            WebRequest request) {
        log.error("Erreur comptabilité: {}", ex.getMessage(), ex);

        HttpStatus status = ex.getErrorCode().equals("PROCEDURE_ERROR") ?
                HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(ApiResponseDTO.error(ex.getMessage(), request.getDescription(false)));
    }

    /**
     * Gère les erreurs de conversion de type d'argument
     */
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleTypeMismatchException(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        log.warn("Erreur de type d'argument: {}", ex.getMessage());

        String message = "Type invalide pour " + ex.getName() + ": " + ex.getValue();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.error(message, request.getDescription(false)));
    }

    /**
     * Gère les exceptions non gérées
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGlobalException(
            Exception ex,
            WebRequest request) {
        log.error("Erreur non gérée: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error(
                        "Erreur serveur: " + ex.getMessage(),
                        request.getDescription(false)
                ));
    }
}
