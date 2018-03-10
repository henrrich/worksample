package com.viaplay.worksample.exception;

public class RateLimitingException extends RuntimeException {

    public RateLimitingException() {
        super();
    }

    public RateLimitingException(String message) {
        super(message);
    }

    public RateLimitingException(String message, Throwable cause) {
        super(message, cause);
    }
}
