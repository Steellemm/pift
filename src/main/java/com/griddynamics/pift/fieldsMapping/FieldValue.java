package com.griddynamics.pift.fieldsMapping;

public interface FieldValue<T> {

    T generate();

    Class<T> getType();

    T getValueFromString(String value);

}
