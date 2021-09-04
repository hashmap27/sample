package com.hashmap27.sample.component.advice;

import com.hashmap27.sample.component.common.ApiCommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Rest API 응답 형태를 공통화 하기 위한 처리
 */
@RestControllerAdvice(basePackages = "com.hashmap27.sample.controller")
public class ResponseEntityCommonAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // Controller 에서 작업이 끝난 Response 를 beforeBodyWrite 로 보낼 것인지 결정.
        // Rest API 이기 때문에 무조건 beforeBodyWrite 로 보내야 하기 때문에 true 처리. default 는 false
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        int status = HttpStatus.OK.value();

        if ( serverHttpResponse instanceof ServletServerHttpResponse ) {
            status = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse().getStatus();
        }

        if(mediaType.isCompatibleWith(MediaType.APPLICATION_JSON) && HttpStatus.valueOf(status).is2xxSuccessful()
            && !(o instanceof ApiCommonResponse) && !(o instanceof String)) {
            return ApiCommonResponse.builder()
                    .status(status)
                    .code("SUCCESS")
                    .message("정상적으로 처리 되었습니다.")
                    .data(o)
                    .build();
        }

        return o;
    }
}
