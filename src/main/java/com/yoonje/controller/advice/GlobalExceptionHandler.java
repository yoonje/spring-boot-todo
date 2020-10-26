package com.yoonje.controller.advice;

import com.yoonje.controller.model.ErrorResponse;
import com.yoonje.exception.InvalidReferenceException;
import com.yoonje.exception.NotClosableException;
import com.yoonje.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse _400(Exception ex) {
        return errorResponse(ex);
    }

    @ExceptionHandler({InvalidReferenceException.class, NotClosableException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse _403(Exception ex) {
        return errorResponse(ex);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse _404(NotFoundException ex) {
        return errorResponse(ex);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse _500(RuntimeException ex) {
        return errorResponse(ex);
    }

    private ErrorResponse errorResponse(Exception e) {
        log.error("{}", e);
        return new ErrorResponse(e.getMessage());
    }

}
