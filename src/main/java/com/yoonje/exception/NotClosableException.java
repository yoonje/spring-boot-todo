package com.yoonje.exception;

import com.yoonje.controller.model.ErrorCause;

public class NotClosableException extends RuntimeException {

    public NotClosableException() {
        super(ErrorCause.NOT_CLOSEABLE.message);
    }

}
