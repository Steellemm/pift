package com.griddynamics.pift.creator;

import com.github.javafaker.Faker;
import com.griddynamics.pift.model.Condition;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public interface DateTypeValue<T> extends TypeValue<T> {

    @Override
    default T generate() {
        return generate(new Condition());
    }

    @Override
    default T generate(Condition condition) {
        ZonedDateTime minZoneDateTime = min(condition);
        long min = minZoneDateTime.toEpochSecond();
        long max = max(condition).toEpochSecond();
        long date = new Faker().number().numberBetween(min, max);
        return generate(ZonedDateTime.ofInstant(Instant.ofEpochSecond(date),
                minZoneDateTime.getZone()));
    }

    @Override
    default T parse(String value) {
        return generate(parseInZone(value));
    }

    T generate(ZonedDateTime dateTime);

    default ZonedDateTime min(Condition condition) {
        String min = condition.getMin();
        if (min == null || min.isEmpty()) {
            return Instant.MIN.atZone(ZoneOffset.UTC);
        }
        return parseInZone(min);
    }

    default ZonedDateTime max(Condition condition) {
        String max = condition.getMin();
        if (max == null || max.isEmpty()) {
            return Instant.MAX.atZone(ZoneOffset.UTC);
        }
        return parseInZone(max);
    }

    default ZonedDateTime parseInZone(String dateString) {
        if (dateString.length() > 10) {
            return ZonedDateTime.ofInstant(DateTimeFormatter.ISO_DATE_TIME.parse(dateString, Instant::from), ZoneOffset.UTC);
        } else {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE).atStartOfDay(ZoneOffset.UTC);
        }
    }

}
