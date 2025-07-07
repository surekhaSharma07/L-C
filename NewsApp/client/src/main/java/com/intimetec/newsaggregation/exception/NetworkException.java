package com.intimetec.newsaggregation.exception;

public class NetworkException extends RuntimeException {
    
    private final int statusCode;
    
    public NetworkException(String message) {
        super(message);
        this.statusCode = -1;
    }
    
    public NetworkException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }
    
//    public NetworkException(String message, int statusCode, Throwable cause) {
//        super(message, cause);
//        this.statusCode = statusCode;
//    }
//
//    public NetworkException(Throwable cause) {
//        super(cause);
//        this.statusCode = -1;
//    }
//
//    public int getStatusCode() {
//        return statusCode;
//    }
//
//
//    public boolean isClientError() {
//        return statusCode >= 400 && statusCode < 500;
//    }
//
//    public boolean isServerError() {
//        return statusCode >= 500 && statusCode < 600;
//    }
} 