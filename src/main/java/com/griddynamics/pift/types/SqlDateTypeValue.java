package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.DateTypeValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

@SuppressWarnings("unused")
public class SqlDateTypeValue implements DateTypeValue<Date> {

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date generate(ZonedDateTime dateTime) {
        return Date.from(dateTime.toInstant());
    }

    @Override
    public String toString(Object value) {
        return "'" + dateFormat.format((Date) value) + "'";
    }

    @Override
    public Class<Date> getType() {
        return Date.class;
    }

}
