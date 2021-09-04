package com.hashmap27.sample.component.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * LocalDate Formatter
 */
public class LocalDateFormatter implements Formatter<LocalDate> {

    @Override
    public LocalDate parse(String s, Locale locale) throws ParseException {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public String print(LocalDate localDate, Locale locale) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate);
    }
}
