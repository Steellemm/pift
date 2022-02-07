package com.griddynamics.pift;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
public class EntityManager {
    private final Map<String, Object> createdEntitiesMap = new HashMap<>();
    private final List<Object> createdEntitiesList = new ArrayList<>();
    private final String url;
    private final String user;
    private final String password;
    private final FieldCreatorManager fieldCreatorManager = new FieldCreatorManager();

    /**
     * Pushes the objects from createdEntitiesList into database and then clears the list.
     */
    public void flush() {
        createdEntitiesList.forEach(this::saveEntity);
        createdEntitiesList.clear();
    }

    public <T> List<T> getList(T entity) {
        Class<T> type = (Class<T>) entity.getClass();
        ReflectionUtils.checkOnTable(type);
        String queryForSelect = SQLUtils.createQueryForSelect(entity);
        List<T> entityList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(queryForSelect)
        ) {
            while (rs.next()) {
                entityList.add(getEntityFromResultSet(type, rs));
            }
            return entityList;
        } catch (Exception e) {
            throw new IllegalStateException("Exception in getList method", e);
        }
    }

    public <T> Optional<T> getById(Class<T> type, Object id) {
        ReflectionUtils.checkOnTable(type);
        String query = SQLUtils.createQueryForSelectById(type, id);
        log.debug(query);
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)
        ) {
            if (rs.isBeforeFirst()) {
                rs.next();
                return Optional.of(getEntityFromResultSet(type, rs));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Exception in getById method", e);
        }
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
            if (fieldCreatorManager.getForeignKey(field).isPresent()) {
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
                        .equals(fieldCreatorManager.getForeignKey(field)
                                .orElseThrow(() -> new IllegalArgumentException("FK doesn't contains in yaml file"))
                                .getTableName()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("FK object has not been created yet"));
    }

    /**
     * Saves received object to the database.
     */
    private void saveEntity(Object entity) {
        executeQuery(SQLUtils.createQueryForInsert(entity, fieldCreatorManager));
    }

    private void executeQuery(String query) {
        log.debug(query);
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement())
        {
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

    private  <T> T getEntityFromResultSet(Class<T> type, ResultSet resultSet) {
        T entityInstance = ReflectionUtils.createInstance(type);
        ReflectionUtils.getColumnFields(type).forEach(field -> setField(entityInstance, resultSet, field));
        return entityInstance;
    }

    private void setField(Object entity, ResultSet resultSet, Field field) {
        try {
            if (fieldCreatorManager.getForeignKey(field).isPresent()){
                Object fkObject = getFkObjectFromCreatedEntitiesList(field);
                ReflectionUtils.setFieldValue(entity, field, ReflectionUtils.getFieldValue(SQLUtils.getIdField(fkObject), fkObject));
            }
            else if (fieldCreatorManager.containsInFieldsMapping(field.getType())) {
                ReflectionUtils.setFieldValue
                        (entity, field, resultSet.getObject(SQLUtils.getColumnName(field)));
            } else {
                ReflectionUtils.setFieldValue(entity, field,
                        ReflectionUtils.getEntityWithId(field.getType(), resultSet.getObject(SQLUtils.getColumnName(field))));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Exception in setField method", e);
        }

    }

}
