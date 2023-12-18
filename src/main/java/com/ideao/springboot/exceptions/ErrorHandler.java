package com.ideao.springboot.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> productNotFoundHandler(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> IdInvalidHandler(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID argument not valid.");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> InvalidFieldsHandler(MethodArgumentNotValidException ex){
        var errors = ex.getFieldErrors();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.stream().map(ErrorFieldRecordDto::new).toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> InvalidJsonHandler(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    private record ErrorFieldRecordDto(String field, String message) {
        private ErrorFieldRecordDto(FieldError error){
           this(error.getField(), error.getDefaultMessage());
        }
    }
}