package com.griddynamics.pift.creator;

import com.github.javafaker.Faker;
import com.griddynamics.pift.ReflectionUtils;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.FieldType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DateFieldCreator implements FieldCreator {
    private final Faker faker = new Faker();

    private final Map<Class<?>, Function<String, Long>> dateToLongConverterMap = Stream.of(new Object[][]{
                    {LocalDate.class, (Function<String, Long>) date -> LocalDate.parse(date).toEpochDay()},
                    {LocalDateTime.class, (Function<String, Long>) date -> LocalDateTime.parse(date).toEpochSecond(ZoneOffset.UTC)},
                    {Timestamp.class, (Function<String, Long>) date -> {
                        try {
                            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Exception in dateToLongConverterMap", e);
                        }
                    }},
                    {java.sql.Date.class, (Function<String, Long>) date -> {
                        try {
                            return new SimpleDateFormat("MM-dd-yyyy").parse(date).getTime();
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Exception in dateToLongConverterMap", e);
                        }
                    }},
            }
    ).collect(Collectors.toMap(data -> (Class<?>) data[0], data -> (Function<String, Long>) data[1]));

    private final Map<Class<?>, Function<Column, Object>> fieldsMapping = Stream.of(new Object[][]{
                    {LocalDate.class, (Function<Column, Object>) column -> {
                        if (column.getCondition() != null) {
                            long offset = ((LocalDate) getMin(column, LocalDate.class)).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                            long end = ((LocalDate) getMax(column, LocalDate.class)).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                            long diff = end - offset + 1;
                            return Instant.ofEpochSecond(offset + (long) (Math.random() * diff)).atZone(ZoneOffset.UTC).toLocalDate();
                        }
                        return LocalDate.from(faker.date().birthday().toInstant());
                    }},
                    {LocalDateTime.class, (Function<Column, Object>) column -> {
                        if (column.getCondition() != null) {
                            long offset = ((LocalDateTime) getMin(column, LocalDateTime.class)).toInstant(ZoneOffset.UTC).getEpochSecond();
                            long end = ((LocalDateTime) getMax(column, LocalDateTime.class)).toInstant(ZoneOffset.UTC).getEpochSecond();
                            long diff = end - offset + 1;
                            return LocalDateTime.ofEpochSecond(offset + (long) (Math.random() * diff), 0, ZoneOffset.UTC);
                        }
                        return LocalDateTime.ofInstant(faker.date().birthday().toInstant(), ZoneId.of("Europe/London"));
                    }},
                    {Timestamp.class, (Function<Column, Object>) column -> {
                        if (column.getCondition() != null) {
                            long offset = ((Timestamp) getMin(column, Timestamp.class)).getTime();
                            long end = ((Timestamp) getMax(column, Timestamp.class)).getTime();
                            long diff = end - offset + 1;
                            return new Timestamp(offset + (long) (Math.random() * diff));
                        }
                        return Timestamp.from(faker.date().birthday().toInstant());
                    }},
                    {java.sql.Date.class, (Function<Column, Object>) column -> {
                        if (column.getCondition() != null) {
                            return new java.sql.Date(faker.date().between((Date) getMin(column, java.sql.Date.class), (Date) getMax(column, java.sql.Date.class)).getTime());
                        }
                        return new java.sql.Date(faker.date().birthday().getTime());
                    }},
            }
    ).collect(Collectors.toMap(data -> (Class<?>) data[0], data -> (Function<Column, Object>) data[1]));

    @Override
    public Object createValue(Field field, Column column) {
        return fieldsMapping.get(field.getType()).apply(column);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.DATE;
    }

    private Object getMin(Column column, Class<?> type) {
        return getDate(type, column.getCondition().getMin());
    }

    private Object getMax(Column column, Class<?> type) {
        return getDate(type, column.getCondition().getMax());
    }

    private Object getDate(Class<?> type, String date) {
        if (date.equals("current")) {
            if (type.isAssignableFrom(LocalDateTime.class)) {
                return ReflectionUtils.createInstance(type, Instant.now().getEpochSecond());
            } else if (type.isAssignableFrom(LocalDate.class)) {
                return ReflectionUtils.createInstance(type, TimeUnit.MILLISECONDS.toDays(Instant.now().toEpochMilli()));
            }
            return ReflectionUtils.createInstance(type, System.currentTimeMillis());
        }
        return ReflectionUtils.createInstance(type, dateToLongConverterMap.get(type).apply(date));
    }
}
