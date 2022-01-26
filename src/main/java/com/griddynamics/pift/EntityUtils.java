package com.griddynamics.pift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.javafaker.Faker;
import com.griddynamics.pift.pojo.ColumnProps;
import com.griddynamics.pift.pojo.Pojo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Function;


@UtilityClass
@Slf4j
public class EntityUtils {
    private final Faker faker = new Faker();

    /**
     * Map for autogenerate random values,
     * where key - class of field type
     * value - lambda that generates value for this type
     */
    private final Map<Class<?>, Function<Field, Object>> fieldsMapping = Map.of(
            Long.class, field -> faker.number().randomNumber(),
            String.class, field -> faker.animal().name(),
            Integer.class, field -> faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
            BigDecimal.class, field -> new BigDecimal(faker.number().randomNumber()),
            java.sql.Date.class, field -> new Date(faker.date().birthday().getTime()),
            java.sql.Timestamp.class, field -> Timestamp.from(Instant.now()),
            LocalDate.class, field -> faker.date().birthday().toInstant().atZone(ZoneId.of("Europe/London")).toLocalDate(),
            LocalDateTime.class, field -> LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Europe/London"))
    );

    /**
     * Creates new instance of type parameter and add it in createdEntitiesList.
     */
    public static <T> T create(Class<T> type, List<Object> createdEntitiesList) {
        T object = ReflectionUtils.createInstance(type);
        createdEntitiesList.add(object);
        try {
            setFields(type, object, createdEntitiesList);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in create method", e);
        }
        return object;
    }

    public <T> T getEntityFromResultSet(Class<T> type, ResultSet resultSet) {
        T entityInstance = ReflectionUtils.createInstance(type);
        ReflectionUtils.getColumnFields(type).forEach(field -> setField(entityInstance, resultSet, field));
        return entityInstance;
    }

    public static void checkOnTable(Class<?> type) {
        if (!type.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("POJO is not reflection of table: " + type.getCanonicalName());
        }
    }

    /**
     * Sets fields in object and his superclasses.
     *
     * @param object which field needs to set.
     */
    private static void setFields(Class<?> type, Object object, List<Object> createdEntitiesList) {
        do {
            Arrays.stream(type.getDeclaredFields()).filter(field -> ReflectionUtils.checkIfFieldFilled(field, object))
                    .forEach(field -> setFieldRandom(object, field, createdEntitiesList));
            type = type.getSuperclass();
        } while (type != Object.class);
    }

    private static void setFieldRandom(Object obj, Field field, List<Object> createdEntitiesList) {
        try {
            if (fieldsMapping.containsKey(field.getType())) {
                ReflectionUtils.setFieldValue(obj, field, fieldsMapping.get(field.getType()).apply(field));
            } else {
                ReflectionUtils.setFieldValue(obj, field, createdEntitiesList.stream()
                        .filter(x -> x.getClass().isAssignableFrom(field.getType()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalCallerException(
                                "Trying to set object that has not been created yet")));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        }
    }

    public static ColumnProps getProps(String tableName, String columnName){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(new File(PATH), Pojo.class)
                    .tables.get(tableName)
                    .columns.get(columnName);
        } catch (Exception e) {
            throw new IllegalArgumentException("Excpetion in getParams method", e);
        }
    }

    private <T> Object getEntityWithId(Class<T> type, Object id) {
        Object obj = ReflectionUtils.createInstance(type);
        ReflectionUtils.setFieldValue(obj, SQLUtils.getIdField(type), id);
        return obj;
    }

    private void setField(Object entity, ResultSet resultSet, Field field){
        try {
            if (fieldsMapping.containsKey(field.getType())) {
                ReflectionUtils.setFieldValue
                        (entity, field, resultSet.getObject(SQLUtils.getColumnName(field)));
            } else {
                ReflectionUtils.setFieldValue(entity, field,
                        getEntityWithId(field.getType(), resultSet.getObject(SQLUtils.getColumnName(field))));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Exception in setField method", e);
        }
    }
}
