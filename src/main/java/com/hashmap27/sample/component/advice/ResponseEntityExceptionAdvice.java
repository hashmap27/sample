package com.hashmap27.sample.component.advice;

import com.hashmap27.sample.component.common.ApiCommonResponse;
import com.hashmap27.sample.component.exception.SampleBadRequestException;
import com.hashmap27.sample.component.exception.SampleConflictException;
import com.hashmap27.sample.component.exception.SampleNotFoundException;
import com.hashmap27.sample.component.exception.SampleRuntimeException;
import com.hashmap27.sample.config.ActiveProfiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;

@Slf4j
@RestControllerAdvice
public class ResponseEntityExceptionAdvice extends ResponseEntityExceptionHandler {

    @Resource
    private ActiveProfiles activeProfiles;

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("#handleHttpRequestMethodNotSupported: {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, ex);
    }

    @Override
    public ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("#handleTypeMismatch: {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.BAD_REQUEST, ex);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("handleMethodArgumentNotValid: {}, ex.getBindingResult().getAllErrors().size(): {} ", ex.getMessage(), ex.getBindingResult().getAllErrors().size(), ex);
        return createResponseEntity(HttpStatus.BAD_REQUEST, ex);
    }

    @Override
    public ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("#handleBindException: {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.BAD_REQUEST, ex);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("#handleHttpMessageNotReadable : {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("#handleConstraintViolationException: {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<Object> handleUnexpectedTypeException(UnexpectedTypeException ex) {
        log.error("#handleUnexpectedTypeException: {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.BAD_REQUEST, "F03422", "parameter.validate.failed", ex.getLocalizedMessage(), null);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Object> handleResourceAccessException(ResourceAccessException ex) {
        log.error("#handleResourceAccessException: {}", ex.toString(), ex);
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(SampleBadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(SampleBadRequestException ex) {
        log.error("#handleBadRequestException: {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.BAD_REQUEST, ex.getCode(), ex.getMessage(), ex.getDebugMessage(), null);
    }

    @ExceptionHandler(SampleNotFoundException.class)
    public ResponseEntity<Object> handlerNotFoundException(SampleNotFoundException ex) {
        log.error("#handlerNotFoundException: {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage(), ex.getDebugMessage(), null);
    }

    @ExceptionHandler(SampleConflictException.class)
    public ResponseEntity<Object> handlerStampConflictException(SampleConflictException ex) {
        log.error("#handlerStampConflictException: {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.CONFLICT, ex.getCode(), ex.getMessage(), ex.getDebugMessage(), null);
    }

    @ExceptionHandler({ RuntimeException.class, SampleRuntimeException.class })
    public ResponseEntity<Object> handlerRuntimeException(RuntimeException ex) {
        log.error("#handlerRuntimeException: {}", ex.getMessage(), ex);
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    /**
     * 에러 응답 객체 생성
     * @param httpStatus HttpStatus Code
     * @param ex 에러 객체
     * @return ResponseEntity 객체
     */
    private ResponseEntity<Object> createResponseEntity(HttpStatus httpStatus, Exception ex) {
        return createResponseEntity(httpStatus, httpStatus.getReasonPhrase(), ex.getMessage(), ex.getLocalizedMessage(), null);
    }

    /**
     * 에러 응답 객체 생성
     * @param httpStatus HttpStatus Code
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param messageDev 에러 메시지(개발용)
     * @param body 응답 바디
     * @return ResponseEntity 객체
     */
    private ResponseEntity<Object> createResponseEntity(HttpStatus httpStatus, String code, String message, String messageDev, Object body) {
        log.error("httpStatusCode: {}, code: {}, message: {}, messageDev: {}, body: {}", httpStatus, code, message, messageDev, body);
        return new ResponseEntity<>(ApiCommonResponse.builder()
                .status(httpStatus.value())
                .code(code)
                .message(message)
                .debugMessage(activeProfiles.isLocal() || activeProfiles.isDevelopment() ? messageDev : null)
                .data(body)
                .build(), new HttpHeaders(), httpStatus);
    }
}
