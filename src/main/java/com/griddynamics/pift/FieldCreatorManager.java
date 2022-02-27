package com.griddynamics.pift;

import com.griddynamics.pift.creator.FieldValueCreator;
import com.griddynamics.pift.creator.TypeValue;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.ForeignKey;
import com.griddynamics.pift.types.TypeValueMap;
import com.griddynamics.pift.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class FieldCreatorManager {

    private final Map<Field, FieldValueCreator> userCreatorByField = new HashMap<>();
    private final PiftPropertiesManager piftPropertiesManager = new PiftPropertiesManager();
    private final TypeValueMap typeValueMap = TypeValueMap.getInstance();
    private final EntityMap entityMap;

    public FieldCreatorManager(EntityMap entityMap) {
        this.entityMap = entityMap;
    }

    public Object getParsedValue(Class<?> type, String value){
        return typeValueMap.get(type).parse(value);
    }

    public Object createValue(Field field) {
        if (userCreatorByField.containsKey(field)) {
            return userCreatorByField.get(field).apply(field);
        }
        Optional<Column> column = getFromProperties(field);
        if (column.isPresent()) {
            TypeValue<?> typeValue = typeValueMap.get(field.getType());
            return column.get().getCondition() == null
                    ? typeValue.generate()
                    : typeValue.generate(column.get().getCondition());
        }
        return typeValueMap.get(field.getType()).generate();
    }

    public Optional<ForeignKey> getForeignKey(Field field) {
        String tableName = ReflectionUtils.getTableName(field);
        String columnName = ReflectionUtils.getColumnName(field);
        return piftPropertiesManager.getForeignKey(tableName, columnName);
    }

    public Object getFieldValue(Field field) {
        Optional<ForeignKey> foreignKey = getForeignKey(field);
        if (foreignKey.isPresent()) {
            return ReflectionUtils.getIdValue(getFkObject(foreignKey.get()));
        }
        if (supportsType(field.getType())) {
            return createValue(field);
        }
        return getFkObject(field.getType());
    }

    private Object getFkObject(ForeignKey foreignKey) {
        String tableName = foreignKey.getTableName();
        return entityMap.getLast(entity -> ReflectionUtils.getTableName(entity.getClass()).equals(tableName))
                .orElseThrow(() -> new IllegalArgumentException("FK object has not been created yet"));
    }

    private Object getFkObject(Class<?> type) {
        return entityMap.getLast(entity -> entity.getClass().isAssignableFrom(type))
                .orElseThrow(() -> new IllegalArgumentException("FK object has not been created yet"));
    }

    public void addValueGenerator(Class<?> type, TypeValue<?> generator) {
        typeValueMap.add(type, generator);
    }

    public void addValueGenerator(Field field, FieldValueCreator fieldValueCreator) {
        userCreatorByField.put(field, fieldValueCreator);
    }

    public boolean supportsType(Class<?> type) {
        return typeValueMap.contains(type);
    }

    public boolean containsInUserCreatorByField(Field field) {
        return userCreatorByField.containsKey(field);
    }

    private Optional<Column> getFromProperties(Field field) {
        String tableName = ReflectionUtils.getTableName(field);
        String columnName = ReflectionUtils.getColumnName(field);
        return piftPropertiesManager.getColumn(tableName, columnName);
    }

}
