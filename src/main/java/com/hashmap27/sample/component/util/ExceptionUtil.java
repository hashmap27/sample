package com.hashmap27.sample.component.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 예외처리 관련 유틸
 */
public class ExceptionUtil {

    public static final HttpStatus DEFAULT_ERROR_HTTP_STATUS_CODE = HttpStatus.INTERNAL_SERVER_ERROR;

    private ExceptionUtil() { /* DO NOTHING */ }

    /** Throwable 내부의 Cause 를 추적하면서 원하는 타입의 Exception 을 찾는다. */
    public static <T extends Throwable> T findInnerException(Throwable e, Class<T> type) {
        Throwable innerEx = e.getCause();
        if(innerEx == null) {
            return null;
        }
        if(innerEx.getClass().equals(type)) {
            return (T) innerEx;
        }
        return findInnerException(innerEx, type);
    }

    /** 예외의 Cause 메시지까지 모두 획득  */
    public static String getMessageIncludeCause(Throwable e) {
        StringBuilder message = new StringBuilder(e.getMessage() == null ? "" : e.getMessage());
        Throwable innerEx = e.getCause();
        while(innerEx != null) {
            message.append(" - " + innerEx.getClass().getSimpleName() + ": " + innerEx.getMessage());
            innerEx = innerEx.getCause();
        }
        return message.toString();
    }

    /** 클래스의 ResponseStatus 어노테이션에서 오류상태코드 값 획득 (찾지 못하면 기본 값, 500 순으로 fallback) */
    public static HttpStatus getResponseStatusAnnotationCode(Throwable e, HttpStatus defaultHttpStatus) {
        ResponseStatus annotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        return (annotation != null) ? annotation.code() : (defaultHttpStatus != null) ? defaultHttpStatus : DEFAULT_ERROR_HTTP_STATUS_CODE;
    }
}
