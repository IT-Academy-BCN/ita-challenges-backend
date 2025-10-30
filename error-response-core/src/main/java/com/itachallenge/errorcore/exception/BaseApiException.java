package com.itachallenge.errorcore.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base class for all user-facing API exceptions.
 * Encapsulates HTTP status, message key (for i18n), and optional message args.
 */
@Getter
public abstract class BaseApiException extends RuntimeException {

    private final HttpStatus status;
    private final String messageKey;
    private final transient Object[] messageArgs;

    protected BaseApiException(HttpStatus status, String messageKey, Object... messageArgs) {
        super(messageKey); // ensures the message field in Throwable is set
        this.status = status;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }
}

