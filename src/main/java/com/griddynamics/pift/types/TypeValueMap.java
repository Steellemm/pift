package com.griddynamics.pift.types;

import com.griddynamics.pift.creator.TypeValue;
import com.griddynamics.pift.utils.ReflectionUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TypeValueMap {

    private final static TypeValueMap INSTANCE = new TypeValueMap();

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
        return INSTANCE;
    }

    public <T> TypeValue<T> get(Class<T> type) {
        if (!fieldValueByType.containsKey(type)) {
            throw new IllegalArgumentException("Unidentified type: " + type);
        }
        return (TypeValue<T>) fieldValueByType.get(type);
    }

    public void add(Class<?> type, TypeValue<?> generator) {
        fieldValueByType.put(type, generator);
    }

    public boolean contains(Class<?> type) {
        return fieldValueByType.containsKey(type);
    }

}
