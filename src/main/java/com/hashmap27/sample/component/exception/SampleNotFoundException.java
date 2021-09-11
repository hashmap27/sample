package com.hashmap27.sample.component.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class SampleNotFoundException extends SampleApiException {

    public SampleNotFoundException(String code) {
        super(HttpStatus.NOT_FOUND, code);
    }

    public SampleNotFoundException(String code, Throwable cause) {
        super(HttpStatus.NOT_FOUND, code, cause);
    }

    public SampleNotFoundException(String code, String message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }

    public SampleNotFoundException(String code, String message, String debugMessage) {
        super(HttpStatus.NOT_FOUND, code, message, debugMessage);
    }

    public SampleNotFoundException(String code, String message, String debugMessage, Throwable cause) {
        super(HttpStatus.NOT_FOUND, code, message, debugMessage, cause);
    }

    public SampleNotFoundException(String code, String message, String debugMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(HttpStatus.NOT_FOUND, code, message, debugMessage, cause, enableSuppression, writableStackTrace);
    }
}
