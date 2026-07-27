package com.gpsroot.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponse {

    private String errorCode;
    private LocalDateTime timestamp;
    private Map<String,String> message;

}
