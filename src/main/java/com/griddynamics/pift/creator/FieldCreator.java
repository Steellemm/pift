package com.griddynamics.pift.creator;

import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.FieldType;

import java.lang.reflect.Field;

public interface FieldCreator {

    Object createValue(Field field, Column column);

    FieldType getFieldType();

}
