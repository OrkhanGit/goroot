package com.gpsroot.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(NotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse();
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("message", ex.getMessage());
        errorResponse.setErrorCode("404_NOT_FOUND");
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setMessage(errorMap);
        HttpHeaders headers = new HttpHeaders();
        headers.add("error-code","404_NOT_FOUND");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(headers)
                .body(errorResponse);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflict (NotFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse();
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("message", ex.getMessage());
        errorResponse.setErrorCode("409_CONFLICT");
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setMessage(errorMap);
        HttpHeaders headers = new HttpHeaders();
        headers.add("error-code","409_CONFLICT");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(headers)
                .body(errorResponse);
    }

}
