package com.hashmap27.sample.domain.vo;

import lombok.*;

import java.util.UUID;

/**
 * 기본 Home Controller 에서 사용하는
 * Test Value Object
 */
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.NONE)
@AllArgsConstructor(access = AccessLevel.NONE)
public class HomeVO {

    /**
     * UUID
     *  - 외부에서 입력 받는 값이 아닌 자동 생성 값.
     *  - 생성자 레벨을 조정함.
     */
    private String uuid;

    /** message */
    private String message;

    @Builder
    public HomeVO(String message) {
        this.uuid = UUID.randomUUID().toString();
        this.message = message;
    }
}
