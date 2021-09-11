package com.hashmap27.sample.component.exception;

public class SampleRuntimeException extends RuntimeException {

    public SampleRuntimeException() {
        super();
    }

    public SampleRuntimeException(String message) {
        super(message);
    }

    public SampleRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SampleRuntimeException(Throwable cause) {
        super(cause);
    }

    public SampleRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
