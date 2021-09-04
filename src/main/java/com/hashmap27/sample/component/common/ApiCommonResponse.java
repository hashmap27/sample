package com.hashmap27.sample.component.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Rest API Common Response 객체
 *  - RestAPI 호출 시 동일한 형태의 Response 전문이 return 될 수 있도록 한다.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.NONE)
@AllArgsConstructor
public class ApiCommonResponse<T> {

    /** HTTP Status Code */
    private int status;

    /** Response Code */
    private String code;

    /** Response Message */
    private String message;

    /** debugging 용 메시지 (운영 환경에서는 전달되지 않음) */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String debugMessage;

    /** 응답 바디 */
    private T data;
}
