package com.griddynamics.pift.creator;

import com.github.javafaker.Faker;
import com.griddynamics.pift.ReflectionUtils;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.FieldType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class DateFieldCreator implements FieldCreator {
    private final Faker faker = new Faker();

    private final Map<Class<?>, String> dateFormatMap = Map.of(
            Timestamp.class, "yyyy-MM-dd HH:mm:ss",
            java.sql.Date.class, "MM-dd-yyyy"
    );

    private final Map<Class<?>, Function<Column, Object>> fieldsMapping = Map.of(
            java.sql.Date.class, column -> {
                if (column.getCondition() != null) {
                    return new java.sql.Date(faker.date().between((Date) getMin(column, java.sql.Date.class), (Date)getMax(column, java.sql.Date.class)).getTime());
                }
                return new java.sql.Date(faker.date().birthday().getTime());
            },
            java.sql.Timestamp.class, column -> {
                if (column.getCondition() != null) {
                    log.debug(getMax(column, Timestamp.class).toString());
                    log.debug(getMin(column, Timestamp.class).toString());
                    long offset = ((Timestamp) getMin(column, Timestamp.class)).getTime();
                    log.debug("offset " + offset);
                    long end = ((Timestamp)getMax(column, Timestamp.class)).getTime();
                    log.debug("end " + end);
                    long diff = end - offset + 1;
                    log.debug("diff " + diff);

                    return new Timestamp(offset + (long)(Math.random() * diff));
                }
                return Timestamp.from(faker.date().birthday().toInstant());
//            },
//            LocalDate.class, column -> {
//                if (column.getCondition() != null) {
//                    return LocalDate.from(faker.date().between(getMin(column, LocalDate.class), getMax(column, LocalDate.class)).toInstant());
//                }
//                return LocalDate.from(faker.date().birthday().toInstant());
//            },
//            LocalDateTime.class, column -> {
//                if (column.getCondition() != null) {
//                    return LocalDateTime.ofInstant(faker.date().between(getMin(column, LocalDateTime.class), getMax(column, LocalDateTime.class)).toInstant(), ZoneId.of("Europe/London"));
//                }
//                return LocalDateTime.ofInstant(faker.date().birthday().toInstant(), ZoneId.of("Europe/London"));
            });

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
        try {
            if (date.equals("current")) {
                return ReflectionUtils.createInstance(type, System.currentTimeMillis());
            }
            SimpleDateFormat smp = new SimpleDateFormat(dateFormatMap.get(type));
            return ReflectionUtils.createInstance(type, smp.parse(date).getTime());
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in getMin method", e);
        }
    }


}
