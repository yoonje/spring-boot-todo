package com.yoonje.exception;

import com.yoonje.controller.model.ErrorCause;

public class InvalidReferenceException extends RuntimeException {

    public InvalidReferenceException(ErrorCause cause) {
        super(cause.message);
    }

}
