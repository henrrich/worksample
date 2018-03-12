package com.viaplay.worksample.exception.handler;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * This class represents the error response body for artistinfo REST API
 */
public class ErrorResponseBody implements Serializable {

    private String timestamp;
    private int status; // http status code
    private String error; // http status text
    private String message; // detailed error message
    private String path; // uri of the incoming request

    public ErrorResponseBody() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        this.timestamp = dateFormat.format(new Date());
    }

    public ErrorResponseBody(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
