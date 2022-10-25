package com.pismo.transaction.pismotransaction.exception;

public class ErrorInfo {

    private String message;

    private String exceptionType;

    public ErrorInfo(String message, String exceptionType) {
        this.message = message;
        this.exceptionType = exceptionType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }
}
