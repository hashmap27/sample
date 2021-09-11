package com.hashmap27.sample.component.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SampleApiException extends SampleRuntimeException {

    /** Rest API 응답 코드(Response 상태 코드) */
    private HttpStatus status;

    /** 에러 코드 */
    private String code;

    /** 에러 메시지 */
    private String message;

    /** 디버깅용 메시지 */
    private String debugMessage;

    public SampleApiException(HttpStatus status, String code) {
        super(code);
        this.status = status;
        this.code = code;
        this.message = code;
    }

    public SampleApiException(HttpStatus status, String code, Throwable cause) {
        super(code, cause);
        this.status = status;
        this.code = code;
        this.message = code;
    }

    public SampleApiException(HttpStatus status, String code, String message) {
        super(code);
        this.status = status;
        this.code = code;
        this.message = message;
        this.debugMessage = message;
    }

    public SampleApiException(HttpStatus status, String code, String message, String debugMessage) {
        super(code);
        this.status = status;
        this.code = code;
        this.message = message;
        this.debugMessage = debugMessage;
    }

    public SampleApiException(HttpStatus status, String code, String message, String debugMessage, Throwable cause) {
        super(code, cause);
        this.status = status;
        this.code = code;
        this.message = message;
        this.debugMessage = debugMessage;
    }

    public SampleApiException(HttpStatus status, String code, String message, String debugMessage, Throwable cause, boolean enableSuppression,  boolean writableStackTrace) {
        super(code, cause, enableSuppression, writableStackTrace);
        this.status = status;
        this.code = code;
        this.message = message;
        this.debugMessage = debugMessage;
    }
}
