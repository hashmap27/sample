package com.hashmap27.sample.component.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Reflection Util
 */
@Slf4j
public class ReflectionUtil {

    private static final List<String> NOT_USER_METHODS = Arrays.asList("getStackTrace", "getCurrentMethodName", "getStackTraceMethodName");

    private ReflectionUtil() { /* DO NOTHING */ }

    /**
     * 현재 메소드명 획득
     */
    public static String getCurrentMethodName() {
        return getStackTraceMethodName(0);
    }

    /**
     * 현재메소드명 또는 호출자의 메소드명을 획득
     * @param upperDepth 0이면 현재메소드명, 1이상이면 상위 호출 depth 메소드명
     * @return 메소드명
     */
    public static String getStackTraceMethodName(final int upperDepth) {
        final StackTraceElement[] stackList = Thread.currentThread().getStackTrace();
        int length = stackList.length;
        var startIndex = 0;
        for(var i = 0; i < length; i++) {
            String methodName = stackList[i].getMethodName();
            if(NOT_USER_METHODS.contains(methodName)) {
                startIndex++;
            } else {
                break;
            }
        }

        return stackList[startIndex + upperDepth].getMethodName();
    }

}
