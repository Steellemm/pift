package com.griddynamics.pift;

import com.griddynamics.pift.utils.ReflectionUtils;
import com.griddynamics.pift.utils.SQLUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
public class EntityManager {
    private final String url;
    private final String user;
    private final String password;
    private final EntityMap entityMap = new EntityMap();
    private final FieldCreatorManager fieldCreatorManager = new FieldCreatorManager(entityMap);

    /**
     * Pushes all objects into database
     */
    public void flush() {
        entityMap.getNotFlushedEntities().forEach(this::saveEntity);
        entityMap.flush();
    }

    public <T> List<T> getList(T entity) {
        Class<T> type = (Class<T>) entity.getClass();
        ReflectionUtils.checkOnTable(type);
        String queryForSelect = SQLUtils.createQueryForSelect(entity);
        List<T> entityList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(queryForSelect)) {
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

    public <T> T createForChain(Class<T> type) {
        return createForChain(type, null);
    }

    public <T> T createForChain(Class<T> type, String entityId) {
        String id = create(type, entityId);
        return entityMap.get(id);
    }

    /**
     * Creates new instance of type class with random values in fields.
     */
    public String create(Class<?> type) {
        return create(type, null);
    }

    public String create(Class<?> type, String entityId) {
        Object object = createFilledObject(type);
        if (entityId == null || entityId.isEmpty()) {
            return entityMap.add(object);
        } else {
            return entityMap.add(object, entityId);
        }
    }

    public <T> T update(T entity, Map<String, String> fieldValues) {
        try {
            setFields(entity, fieldValues);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in create method", e);
        }
        return entity;
    }

    /**
     * Creates new instance of type parameter and add it in createdEntitiesList.
     */
    private <T> T createFilledObject(Class<T> type) {
        try {
            T object = ReflectionUtils.createInstance(type);
            setFields(object);
            return object;
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in create method", e);
        }
    }

    /**
     * Sets fields in object and his superclasses.
     *
     * @param object which field needs to set.
     */
    private void setFields(Object object) {
        setFields(object, Collections.emptyMap());
    }

    private void setFields(Object object, Map<String, String> valueMap) {
        ReflectionUtils.getColumnFields(object.getClass())
                .forEach(field -> {
                    if (valueMap.containsKey(ReflectionUtils.getColumnName(field))) {
                        ReflectionUtils.setFieldValue(
                                object,
                                field,
                                fieldCreatorManager.getParsedValue(field.getType(), valueMap.get(field.getName())));
                    } else {
                        setFieldRandom(object, field);
                    }
                });
    }

    private void setFieldRandom(Object obj, Field field) {
        ReflectionUtils.setFieldValue(obj, field, fieldCreatorManager.getFieldValue(field));
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
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in connect method", e);
        }
    }

    private  <T> T getEntityFromResultSet(Class<T> type, ResultSet resultSet) {
        T entityInstance = ReflectionUtils.createInstance(type);
        ReflectionUtils.getColumnFields(type)
                .forEach(field -> ReflectionUtils.setFieldValue(entityInstance, field, getFieldValue(resultSet, field)));
        return entityInstance;
    }

    private Object getFieldValue(ResultSet resultSet, Field field) {
        try {
            Class<?> type = field.getType();
            String columnName = ReflectionUtils.getColumnName(field);
            if (fieldCreatorManager.supportsType(type)) {
                return resultSet.getObject(columnName, type);
            }
            ReflectionUtils.checkOnTable(type);
            Object foreignEntity = ReflectionUtils.createInstance(type);
            ReflectionUtils.setIdField(foreignEntity, resultSet.getObject(columnName));
            return foreignEntity;
        } catch (Exception e) {
            throw new IllegalStateException("Exception in setField method", e);
        }
    }
}
