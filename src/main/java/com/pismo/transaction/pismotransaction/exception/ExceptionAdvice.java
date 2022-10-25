package com.pismo.transaction.pismotransaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(NoRecordFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo noRecordFoundHandler(NoRecordFoundException ex) {
        return new ErrorInfo(ex.getMessage(), ex.getClass().getSimpleName());
    }

    @ResponseBody
    @ExceptionHandler(DuplicateRecordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo duplicateRecordHandler(DuplicateRecordException ex) {
        return new ErrorInfo(ex.getMessage(), ex.getClass().getSimpleName());
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo ConstraintViolationHandler(ConstraintViolationException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getConstraintViolations().stream()
                .forEach((e) -> errorMessage.append(e.getMessage()));

        return new ErrorInfo(errorMessage.toString(), ex.getClass().getSimpleName());
    }

    @ResponseBody
    @ExceptionHandler(InvalidTransactionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo invalidTransactionHandler(InvalidTransactionException ex) {
        return new ErrorInfo(ex.getMessage(), ex.getClass().getSimpleName());
    }

}
