package com.griddynamics.pift;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
public class EntityManager {
    private final Map<String, Object> createdEntitiesMap = new HashMap<>();
    private final List<Object> createdEntitiesList = new ArrayList<>();
    private final FieldCreatorManager fieldCreatorManager = new FieldCreatorManager();
    private final String URL;
    private final String USER;
    private final String PASSWORD;

    /**
     * Pushes the objects from createdEntitiesList into database and then clears the list.
     */
    public void flush() {
        createdEntitiesList.forEach(this::saveEntity);
        createdEntitiesList.clear();
    }

    /**
     * Creates new instance of type class with random values in fields.
     *
     * @param type of the entity class.
     * @return new instance of type class.
     */
    public <T> T create(Class<T> type) {
        return create(type, type.getSimpleName());
    }

    public <T> T create(Class<T> type, String entityId) {
        log.debug(createdEntitiesMap.keySet().toString());
        T object = createFilledObject(type);
        createdEntitiesMap.put(getEntityClassName(entityId), object);
        return object;
    }

    /**
     * Creates new instance of type parameter and add it in createdEntitiesList.
     */
    private <T> T createFilledObject(Class<T> type) {
        T object = ReflectionUtils.createInstance(type);
        createdEntitiesList.add(object);
        try {
            setFields(type, object);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in create method", e);
        }
        return object;
    }

    /**
     * Sets fields in object and his superclasses.
     *
     * @param object which field needs to set.
     */
    private void setFields(Class<?> type, Object object) {
        do {
            Arrays.stream(type.getDeclaredFields()).filter(field -> ReflectionUtils.checkIfFieldFilled(field, object))
                    .forEach(field -> setFieldRandom(object, field));
            type = type.getSuperclass();
        } while (type != Object.class);
    }

    private void setFieldRandom(Object obj, Field field) {
        if (fieldCreatorManager.existInProperties(field)) {
            if (fieldCreatorManager.getForeignKeyTableName(field).isPresent()) {
                Object fkObject = getFkObjectFromCreatedEntitiesList(field);
                ReflectionUtils.setFieldValue(obj, field, ReflectionUtils.getFieldValue(SQLUtils.getIdField(fkObject), fkObject));
            } else {
                ReflectionUtils.setFieldValue(obj, field, fieldCreatorManager.createValue(field));
            }
        } else {
            if (!fieldCreatorManager.containsInFieldsMapping(field.getType())) {
                ReflectionUtils.setFieldValue(obj, field, createdEntitiesList.stream()
                        .filter(x -> x.getClass().isAssignableFrom(field.getType()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Trying to set object that has not been created yet")));
            } else {
                ReflectionUtils.setFieldValue(obj, field, fieldCreatorManager.createValue(field));
            }
        }
    }

    private Object getFkObjectFromCreatedEntitiesList(Field field){
        return createdEntitiesList.stream()
                .filter(entity -> ReflectionUtils.getTableName(entity.getClass())
                        .equals(fieldCreatorManager.getForeignKeyTableName(field).orElse("")))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("FK object has not been created yet"));
    }

    /**
     * Saves received object to the database.
     */
    private void saveEntity(Object entity) {
        executeQuery(SQLUtils.createQueryForInsert(entity));
    }

    private void executeQuery(String query) {
        log.debug(query);
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in connect method", e);
        }
    }

    private String getEntityClassName(String entityClassName) {
        String str = entityClassName;
        int counter = 0;
        while (createdEntitiesMap.containsKey(entityClassName)) {
            entityClassName = str + (++counter);
        }
        return entityClassName;
    }

}
