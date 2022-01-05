package com.griddynamics.uspanov.test;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.griddynamics.uspanov.test.ReflectionUtils.*;


@UtilityClass
@Slf4j
public class EntityUtils {
    private final Faker faker = new Faker();

    private final Map<Class<?>, Function<Field, Object>> map = Map.of(
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

    public static <T> List<T> create(Class<T> type) {
        T object = createInstance(type);
        List<T> list = new ArrayList<>(List.of(object));
        try {
            setFields(type.getDeclaredFields(), object, list);
            setFields(type.getSuperclass().getDeclaredFields(), object, list);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in create method", e);
        }
        return list;
    }

    public static void setField(Object obj, Field field, Long id) {
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

    private static <T> T createInstance(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in createInstance method", e);
        }
    }

    private static <T> void setFields(Field[] fields, Object object, List<T> list){
        Arrays.stream(fields).filter(x -> !x.isAnnotationPresent(Id.class))
                .forEach(field -> list.addAll((List<? extends T>) setField(object, field)));
    }

    private static List<?> setField(Object obj, Field field) {
        boolean accessStatus = field.canAccess(obj);
        try {
            field.setAccessible(true);
            if (map.containsKey(field.getType())) {
                field.set(obj, map.get(field.getType()).apply(field));
            }
            // NEEDS TO ADD CONDITION FOR HANDLING RELATIONS BETWEEN ENTITIES
            // causes infinite recursion
            else {
                List<?> list = create(field.getType());
                field.set(obj, list.get(0));
                return list;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        } finally {
            field.setAccessible(accessStatus);
        }
    }
}
