package com.griddynamics.uspanov.test;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.griddynamics.uspanov.test.ReflectionUtils.*;


@UtilityClass
@Slf4j
public class EntityUtils {
    private final Faker faker = new Faker();

    private final Map<Class<?>, Function<Field, Object>> fieldsMapping = Map.of(
            Long.class, field -> faker.number().randomNumber(),
            String.class, field -> faker.animal().name(),
            Integer.class, field -> faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
            BigDecimal.class, field -> new BigDecimal(faker.number().randomNumber())
    );

    public String createInsertQuery(String tableName, Class<?> type) {
        StringBuilder insertQuery = new StringBuilder("insert into " + tableName + " (" +
                getColumnNameStream(type).collect(Collectors.joining(",")) + ") values (");

        getColumnNameStream(type).forEach(x -> insertQuery.append(":").append(x).append(", "));
        insertQuery.setCharAt(insertQuery.lastIndexOf(","), ')');

        return insertQuery.toString();
    }

    public static <T> T create(Class<T> type, List<Object> createdEntitiesList) {
        T object = createInstance(type);
        createdEntitiesList.add(object);
        try {
            setFields(type.getDeclaredFields(), object, createdEntitiesList);
            setSuperClassFields(type, object, createdEntitiesList);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in create method", e);
        }
        return object;
    }

    public static void setFieldRandom(Object obj, Field field, Object id) {
        boolean accessStatus = field.canAccess(obj);
        try {
            field.setAccessible(true);
            field.set(obj, id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        } finally {
            field.setAccessible(accessStatus);
        }
    }

    private static void setSuperClassFields(Class<?> type, Object object, List<Object> list) {
        while (!type.getSuperclass().getSimpleName().equals("Object")) {
            setFields(type.getSuperclass().getDeclaredFields(), object, list);
            type = type.getSuperclass();
        }
    }

    private static <T> T createInstance(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in createInstance method", e);
        }
    }

    private static void setFields(Field[] fields, Object object, List<Object> list) {
        Arrays.stream(fields).filter(field -> checkIfFieldFilled(field, object))
                .forEach(field -> setFieldRandom(object, field, list, fields));
    }

    private static void setFieldRandom(Object obj, Field field, List<Object> createdEntitiesList, Field[] fields) {
        boolean accessStatus = field.canAccess(obj);
        try {
            field.setAccessible(true);
            if (fieldsMapping.containsKey(field.getType())) {
                field.set(obj, fieldsMapping.get(field.getType()).apply(field));
            } else {
                field.set(obj, createdEntitiesList.stream()
                        .filter(x -> x.getClass().isAssignableFrom(field.getType()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalCallerException(
                                "Trying to set object that has not been created yet")));

                setForeignKey(fields, field, obj);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        } finally {
            field.setAccessible(accessStatus);
        }
    }

    private void setForeignKey(Field[] fields, Field field, Object obj) throws IllegalAccessException {
        Field foreignKey = Arrays.stream(fields).filter(field1 ->
                        field1.getName().equals(field.getAnnotation(JoinColumn.class).name()))
                .findFirst()
                .get();

        setFieldRandom(obj, foreignKey, getFieldValue(Arrays
                .stream(field.get(obj).getClass().getDeclaredFields())
                .filter(field1 -> field1.isAnnotationPresent(Id.class))
                .findFirst().get(), field.get(obj)));
    }
}
