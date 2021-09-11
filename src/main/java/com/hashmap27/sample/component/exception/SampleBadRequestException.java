package com.hashmap27.sample.component.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SampleBadRequestException extends SampleApiException {

    public SampleBadRequestException(String code) {
        super(HttpStatus.BAD_REQUEST, code);
    }

    public SampleBadRequestException(String code, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, code, cause);
    }

    public SampleBadRequestException(String code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }

    public SampleBadRequestException(String code, String message, String debugMessage) {
        super(HttpStatus.BAD_REQUEST, code, message, debugMessage);
    }

    public SampleBadRequestException(String code, String message, String debugMessage, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, code, message, debugMessage, cause);
    }

    public SampleBadRequestException(String code, String message, String debugMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(HttpStatus.BAD_REQUEST, code, message, debugMessage, cause, enableSuppression, writableStackTrace);
    }

    public SampleBadRequestException(HttpStatus status, String code) {
        super(status, code);
    }
}
