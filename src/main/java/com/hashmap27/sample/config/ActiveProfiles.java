package com.hashmap27.sample.config;

import com.hashmap27.sample.config.type.ActiveProfileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Spring Active Profiles Bean
 * 환경에 따른 분기 처리 등 사용하기 위한 Util
 */

@Configuration
@Slf4j
public class ActiveProfiles implements InitializingBean {

    @Resource
    Environment environment;

    /** 환경정보에서 Active Profile 목록을 추출한다. */
    public List<String> getActiveProfiles() {
        return Arrays.asList(environment.getActiveProfiles());
    }

    /** 환경정보에서 Default Profile 목록을 추출한다. */
    public List<String> getDefaultProfiles() {
        return Arrays.asList(environment.getDefaultProfiles());
    }

    /** 운영 환경(Production)인가? */
    public boolean isProduction() {
        return is(ActiveProfileType.PRODUCTION);
    }

    /** 스테이징 환경(Stage)인가? */
    public boolean isStage() {
        return is(ActiveProfileType.STAGE);
    }

    /** 개발 환경(Development)인가? */
    public boolean isDevelopment() {
        return is(ActiveProfileType.DEVELOPMENT);
    }

    /** 로컬 환경(Local)인가? */
    public boolean isLocal() {
        return is(ActiveProfileType.LOCAL);
    }

    /** 주어진 profile 이 Spring Active Profile 에 포함하는지 여부 */
    public boolean is(ActiveProfileType activeProfileType) {
        return getActiveProfiles().contains(activeProfileType.name().toLowerCase());
    }

    /** 서버 Spring Profile */
    private ActiveProfileType activeProfileType = null;
    /** 서버 Spring Profile */
    public ActiveProfileType getActiveProfileType() {
        return activeProfileType;
    }
    /** 서버 Spring Profile 초기화 */
    private void initActiveProfile() {
        Optional<ActiveProfileType> optionalActiveProfileType = EnumUtils.getEnumList(ActiveProfileType.class)
                .stream().filter(item -> is(item)).findFirst();
        activeProfileType = optionalActiveProfileType.isPresent() ? optionalActiveProfileType.get() : null;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        initActiveProfile();
        log.info(
                "\r\n=====================================" +
                "\r\n# Spring Active Profiles: " + getActiveProfiles() +
                "\r\n# Spring Default Profiles: " + getDefaultProfiles() +
                "\r\n====================================="
        );
    }
}
