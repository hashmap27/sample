package com.hashmap27.sample.config;

import com.hashmap27.sample.component.formatter.LocalDateFormatter;
import com.hashmap27.sample.component.formatter.LocalDateTimeFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Web MVC Configuration
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Formatter 추가.
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Deserialize 시 사용할 formatter
        registry.addFormatter(new LocalDateFormatter());
        registry.addFormatter(new LocalDateTimeFormatter());
        // Serialize 시에는 Application 설정으로 처리.
    }
}
