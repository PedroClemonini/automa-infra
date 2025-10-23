package com.ifsp.gru.oficinas4.infra.automa_infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UnauthorizedExcepetion.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Map<String,String>> handleUnauthorizedException(UnauthorizedExcepetion exception){
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Credenciais não autorizadas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    };


        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<Map<String, String>> handleGenericException(Exception exception) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

