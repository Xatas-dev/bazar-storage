package org.bazar.bazarstorage.adapter.inbound.rest;

import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.node.exception.FileValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<?> handleFileValidationException(FileValidationException e) {
        return ResponseEntity.badRequest().body(e.getValidationErrors());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(e.getMessage());
    }
}
