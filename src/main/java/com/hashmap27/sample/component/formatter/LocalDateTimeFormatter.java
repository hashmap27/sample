package com.hashmap27.sample.component.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * LocalDateTime Formatter
 */
public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {
    @Override
    public LocalDateTime parse(String s, Locale locale) throws ParseException {
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String print(LocalDateTime localDateTime, Locale locale) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
    }
}
