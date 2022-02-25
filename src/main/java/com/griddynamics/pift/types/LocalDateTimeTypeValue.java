package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.DateTypeValue;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@SuppressWarnings("unused")
public class LocalDateTimeTypeValue implements DateTypeValue<LocalDateTime> {

    @Override
    public LocalDateTime generate(ZonedDateTime dateTime) {
        return dateTime.toLocalDateTime();
    }

    @Override
    public Class<LocalDateTime> getType() {
        return LocalDateTime.class;
    }

    @Override
    public LocalDateTime parse(String value) {
        return LocalDateTime.parse(value);
    }
}
