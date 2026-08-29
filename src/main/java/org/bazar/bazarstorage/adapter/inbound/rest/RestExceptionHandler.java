package org.bazar.bazarstorage.adapter.inbound.rest;

import org.bazar.bazarstorage.app.api.exception.BusinessException;
import org.bazar.bazarstorage.app.api.exception.InternalException;
import org.bazar.bazarstorage.app.api.node.exception.NodeValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.bazar.bazarstorage.app.api.exception.ErrorCode.INTERNAL_ERROR;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NodeValidationException.class)
    public ResponseEntity<?> handleFileValidationException(NodeValidationException e) {
        return ResponseEntity.badRequest().body(e.getValidationErrors());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(e.getMessage());
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<Object> handleInternalException(InternalException e) {
        return ResponseEntity
                .status(INTERNAL_ERROR.getStatus())
                .body(INTERNAL_ERROR.formatMessage());
    }
}
