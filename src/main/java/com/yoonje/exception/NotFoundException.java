package com.yoonje.exception;

import com.yoonje.controller.model.ErrorCause;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private Long id;

    public NotFoundException(Long id) {
        super(ErrorCause.TO_DO_NOT_FOUND.message);
        this.id = id;
    }

}
