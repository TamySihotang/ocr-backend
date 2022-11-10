package com.danamon.exception;


import com.danamon.enums.StatusCode;

public class ApplicationException extends RuntimeException {

    private StatusCode statusCode;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, StatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
