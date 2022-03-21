package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.TypeValue;
import com.griddynamics.pift.utils.ReflectionUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TypeValueMap {

    private static TypeValueMap INSTANCE;

    /**
     * Map for autogenerate random values,
     * where key - class of field type
     * value - lambda that generates value for this type
     */
    private final Map fieldValueByType = ReflectionUtils
            .getClassHeirs(TypeValue.class, "com.griddynamics.pift.types")
            .collect(Collectors.toMap(TypeValue::getType, Function.identity()));

    private TypeValueMap() {
    }

    public static TypeValueMap getInstance() {
        TypeValueMap localInstance = INSTANCE;
        if (localInstance == null) {
            synchronized (TypeValueMap.class) {
                localInstance = INSTANCE;
                if (localInstance == null) {
                    INSTANCE = localInstance = new TypeValueMap();
                }
            }
        }
        return localInstance;
    }

    public <T> TypeValue<T> get(Class<T> type) {
        if (!fieldValueByType.containsKey(type)) {
            throw new IllegalArgumentException("Unidentified type: " + type);
        }
        return (TypeValue<T>) fieldValueByType.get(type);
    }

    public String toString(Object value) {
        return get(value.getClass()).toString(value);
    }

    public void add(Class<?> type, TypeValue<?> generator) {
        fieldValueByType.put(type, generator);
    }

    public boolean contains(Class<?> type) {
        return fieldValueByType.containsKey(type);
    }

}
