package com.viaplay.worksample.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viaplay.worksample.exception.ArtistNotFoundException;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.unit.util.UriUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { ArtistNotFoundException.class })
    protected ResponseEntity<Object> handleArtistNotFoundException(RuntimeException ex, WebRequest request) throws Exception {
        String mbid = UriUtil.getLastUriSegment(request.getDescription(false));
        String message = "Artist with MBID " + mbid + " not found!";
        String bodyOfResponse = getErrorResponseBodyInJson(HttpStatus.NOT_FOUND, message);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { RateLimitingException.class })
    protected ResponseEntity<Object> handleRateLimitingException(RuntimeException ex, WebRequest request) throws Exception {
        String message = "Hit rate limit for MusicBrainz server (around 1 request per second), please try again.";
        String bodyOfResponse = getErrorResponseBodyInJson(HttpStatus.SERVICE_UNAVAILABLE, message);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    private String getErrorResponseBodyInJson(HttpStatus status, String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(new ErrorResponseBody(status.value(), message));
    }
}
