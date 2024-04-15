package com.api.almaeng2.global.exception;

public class ApiException extends RuntimeException {

    private final ErrorType errorType;

    public ApiException(ErrorType errorType){
        super(errorType.getMsg());
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
