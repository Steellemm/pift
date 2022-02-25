package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.DateTypeValue;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

@SuppressWarnings("unused")
public class SqlDateTypeValue implements DateTypeValue<Date> {

    @Override
    public Date generate(ZonedDateTime dateTime) {
        return Date.from(dateTime.toInstant());
    }

    @Override
    public Class<Date> getType() {
        return Date.class;
    }

}
