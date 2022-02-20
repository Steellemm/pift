package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.DateTypeValue;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@SuppressWarnings("unused")
public class LocalDateTypeValue implements DateTypeValue<LocalDate> {

    @Override
    public LocalDate generate(ZonedDateTime dateTime) {
        return dateTime.toLocalDate();
    }

    @Override
    public Class<LocalDate> getType() {
        return LocalDate.class;
    }

}
