package com.yoonje.controller.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCause {

    CROSS_REFERENCE("cross reference is not available"),
    SELF_REFERENCE("self reference is not available"),
    REFERENCE_NOT_FOUND("referred todo is not found"),

    NOT_CLOSEABLE("can not close because referred todo is still open"),

    TO_DO_NOT_FOUND("todo doesn't exist");

    public final String message;

}
