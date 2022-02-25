package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.DateTypeValue;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@SuppressWarnings("unused")
public class TimestampTypeValue implements DateTypeValue<Timestamp> {

    @Override
    public Timestamp generate(ZonedDateTime zonedDateTime) {
        return Timestamp.from(zonedDateTime.toInstant());
    }

    @Override
    public Class<Timestamp> getType() {
        return Timestamp.class;
    }

}
