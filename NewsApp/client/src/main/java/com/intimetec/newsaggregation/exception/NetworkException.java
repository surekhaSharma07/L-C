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
} 