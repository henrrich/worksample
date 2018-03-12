package com.viaplay.worksample.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viaplay.worksample.exception.ArtistNotFoundException;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.util.UriUtil;
import com.viaplay.worksample.util.throttling.RateLimitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/*
 * Global exception handlers are defined in this class to transform the corresponding exceptions into proper error responses
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    RateLimitHandler rateLimitHandler;

    // exception handler for ArtistNotFoundException exception, transform to 404 error
    @ExceptionHandler(value = { ArtistNotFoundException.class })
    protected ResponseEntity<Object> handleArtistNotFoundException(RuntimeException ex, WebRequest request) throws Exception {
        String uri = UriUtil.getRequestUri(request);
        String mbid = UriUtil.getLastUriSegment(uri);
        String message = "Artist with MBID " + mbid + " not found!";
        String bodyOfResponse = getErrorResponseBodyInJson(HttpStatus.NOT_FOUND, message, uri);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    // exception handler for RateLimitingException exception, transform to 503 error
    @ExceptionHandler(value = { RateLimitingException.class })
    protected ResponseEntity<Object> handleRateLimitingException(RuntimeException ex, WebRequest request) throws Exception {
        String message = ex.getMessage();
        String uri = UriUtil.getRequestUri(request);
        String bodyOfResponse = getErrorResponseBodyInJson(HttpStatus.SERVICE_UNAVAILABLE, message, uri);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-RateLimit-Limit", String.valueOf(rateLimitHandler.getRateLimitPerSec()));
        return handleExceptionInternal(ex, bodyOfResponse,
                httpHeaders, HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    // exception handler for all other RuntimeException exceptions, tranform to 500 error
    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) throws Exception {
        String message = ex.getMessage();
        String uri = UriUtil.getRequestUri(request);
        String bodyOfResponse = getErrorResponseBodyInJson(HttpStatus.INTERNAL_SERVER_ERROR, message, uri);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // exception handler for ConstraintViolationException exception triggered by parameter validation, tranform to 400 error
    @ExceptionHandler(value = {ConstraintViolationException.class })
    protected ResponseEntity<Object> handleRequestValidationException(ConstraintViolationException ex, WebRequest request) throws Exception {
        String uri = UriUtil.getRequestUri(request);
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        violations.stream().forEach( v -> message.append(v.getMessage()).append("\n"));
        String bodyOfResponse = getErrorResponseBodyInJson(HttpStatus.BAD_REQUEST, message.toString(), uri);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private String getErrorResponseBodyInJson(HttpStatus status, String message, String path) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(new ErrorResponseBody(status.value(), status.getReasonPhrase(), message, path));
    }
}
