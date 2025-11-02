package com.itachallenge.errorcore.exception;


import lombok.Getter;

/**
 * Base class for all user-facing API exceptions.
 * Encapsulates HTTP status, message key (for i18n), and optional message args.
 */
@Getter
public abstract class BaseApiException extends RuntimeException {

    private final transient ApiCustomErrorInfo info;

    protected BaseApiException(String message, ApiCustomErrorInfo info) {
        super(message);
        this.info = info;// ensures the message field in Throwable is set
    }

}

