package com.hashmap27.sample.component.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class SampleConflictException extends SampleApiException {

    public SampleConflictException(String code) {
        super(HttpStatus.CONFLICT, code);
    }

    public SampleConflictException(String code, Throwable cause) {
        super(HttpStatus.CONFLICT, code, cause);
    }

    public SampleConflictException(String code, String message) {
        super(HttpStatus.CONFLICT, code, message);
    }

    public SampleConflictException(String code, String message, String debugMessage) {
        super(HttpStatus.CONFLICT, code, message, debugMessage);
    }

    public SampleConflictException(String code, String message, String debugMessage, Throwable cause) {
        super(HttpStatus.CONFLICT, code, message, debugMessage, cause);
    }

    public SampleConflictException(String code, String message, String debugMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(HttpStatus.CONFLICT, code, message, debugMessage, cause, enableSuppression, writableStackTrace);
    }
}
