package com.viaplay.worksample.exception;

public class CoverArtNotFoundException extends RuntimeException {

    public CoverArtNotFoundException() {
        super();
    }

    public CoverArtNotFoundException(String message) {
        super(message);
    }

    public CoverArtNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
