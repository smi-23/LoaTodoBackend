package com.loatodo.loatodobackend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage());
        }
        return ErrorResponse.toResponseEntityValid(sb.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("UsernameNotFoundException: {}", e.getMessage());
        return ErrorResponse.toResponseEntity(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();
        log.error("Unhandled Exception: {}", e.getMessage());
        return ErrorResponse.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
    }
}
