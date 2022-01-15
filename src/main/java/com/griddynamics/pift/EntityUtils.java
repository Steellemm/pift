package com.griddynamics.pift;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;



@UtilityClass
@Slf4j
public class EntityUtils {
    private final Faker faker = new Faker();

    /***
     * Map for autogenerate random values,
     * where key - class of field type
     * value - lambda that generates value for this type
     */
    private final Map<Class<?>, Function<Field, Object>> fieldsMapping = Map.of(
            Long.class, field -> faker.number().randomNumber(),
            String.class, field -> faker.animal().name(),
            Integer.class, field -> faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
            BigDecimal.class, field -> new BigDecimal(faker.number().randomNumber())
    );

    /***
     * Creates new instance of type parameter and add it in createdEntitiesList.
     * @param type object.
     * @param createdEntitiesList list of created entities.
     * @return new instance of type class with random values in fields.
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

    /***
     * Sets fields in object and his superclasses.
     * @param type object.
     * @param object which field needs to set.
     * @param createdEntitiesList list of created entities.
     */
    private static void setFields(Class<?> type, Object object, List<Object> createdEntitiesList) {
        do {
            Arrays.stream(type.getDeclaredFields()).filter(field -> ReflectionUtils.checkIfFieldFilled(field, object))
                    .forEach(field -> setFieldRandom(object, field, createdEntitiesList));
            type = type.getSuperclass();
        } while (type != Object.class);
    }

    /***
     * Sets field random value.
     * @param obj object.
     * @param field that needs to set.
     * @param createdEntitiesList list of created entities.
     */
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
}
