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

    public static <T> T create(Class<T> type, List<Object> createdEntitiesList) {
        T object = createInstance(type);
        createdEntitiesList.add(object);
        try {
            setFields(type, object, createdEntitiesList);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in create method", e);
        }
        return object;
    }

    private static void setFields(Class<?> type, Object object, List<Object> list) {
        do {
            Arrays.stream(type.getDeclaredFields()).filter(field -> checkIfFieldFilled(field, object))
                    .forEach(field -> setFieldRandom(object, field, list));
            type = type.getSuperclass();
        } while (type != Object.class);
    }

    private static void setFieldRandom(Object obj, Field field, List<Object> createdEntitiesList) {
        try {
            if (fieldsMapping.containsKey(field.getType())) {
                setFieldValue(obj, field, fieldsMapping.get(field.getType()).apply(field));
            } else {
                setFieldValue(obj, field, createdEntitiesList.stream()
                        .filter(x -> x.getClass().isAssignableFrom(field.getType()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalCallerException(
                                "Trying to set object that has not been created yet")));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        }
    }

    private void setForeignKey(Field[] fields, Field field, Object obj) throws IllegalAccessException {
        Field foreignKey = Arrays.stream(fields).filter(field1 ->
                        field1.getName().equals(field.getAnnotation(JoinColumn.class).name()))
                .findFirst()
                .get();

        setFieldValue(obj, foreignKey, getFieldValue(Arrays
                .stream(field.get(obj).getClass().getDeclaredFields())
                .filter(field1 -> field1.isAnnotationPresent(Id.class))
                .findFirst().get(), field.get(obj)));
    }
}
