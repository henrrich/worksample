package com.viaplay.worksample.exception;

public class ArtistNotFoundException extends RuntimeException {

    public ArtistNotFoundException() {
        super();
    }

    public ArtistNotFoundException(String message) {
        super(message);
    }

    public ArtistNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
