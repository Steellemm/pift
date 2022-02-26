package com.griddynamics.pift;

import com.griddynamics.pift.utils.JsonUtils;
import com.griddynamics.pift.utils.ReflectionUtils;
import com.griddynamics.pift.utils.SQLUtils;
import com.griddynamics.pift.utils.TemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;


@Slf4j
public class EntityManager {
    private final String url;
    private final String user;
    private final String password;
    private final Map<String, Class<?>> userEntityByName;
    private final EntityMap entityMap = new EntityMap();
    private final FieldCreatorManager fieldCreatorManager = new FieldCreatorManager(entityMap);

    private EntityManager(String url, String user, String password, Map<String, Class<?>> userEntityByName) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.userEntityByName = userEntityByName;
    }

    public static EntityManagerBuilder builder() {
        return new EntityManagerBuilder();
    }

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
        T instance = ReflectionUtils.createEntityWithId(type, id);
        return getList(instance).stream().findFirst();
    }

    public Object create(String tableName) {
        return create(tableName, "");
    }

    public Object create(String tableName, String entityId) {
        return create(tableName, Collections.emptyMap(), entityId);
    }

    public Object create(String tableName, Map<String, String> values, String entityId) {
        if (!userEntityByName.containsKey(tableName)) {
            throw new IllegalArgumentException("Entity with passed table name does not exist: " + tableName);
        }
        return create(userEntityByName.get(tableName), values, entityId);
    }

    public <T> T create(Class<T> type) {
        return create(type, "");
    }

    public <T> T create(Class<T> type, String entityId) {
        return create(type, Collections.emptyMap(), entityId);
    }

    public <T> T create(Class<T> type, Map<String, String> values) {
        return create(type, values, "");
    }

    public <T> T create(Class<T> type, Map<String, String> values, String entityId) {
        String id = createAndGetName(type, values, entityId);
        return entityMap.get(id);
    }

    /**
     * Creates new instance of type class with random values in fields.
     */
    public String createAndGetName(Class<?> type, Map<String, String> values, String entityId) {
        Object object = ReflectionUtils.createInstance(type);
        setFields(object, values);
        if (entityId == null || entityId.isEmpty()) {
            return entityMap.add(object);
        } else {
            return entityMap.add(object, entityId);
        }
    }

    public <T> T update(T entity, Map<String, String> fieldValues) {
        setFields(entity, fieldValues);
        return entity;
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
            return ReflectionUtils.createEntityWithId(type, resultSet.getObject(columnName));
        } catch (Exception e) {
            throw new IllegalStateException("Exception in setField method", e);
        }
    }

    public void assertJsonEquals(String fileName, Object json) {
        assertJsonEquals(fileName, JsonUtils.objectToJson(json));
    }

    public void assertJsonEquals(String fileName, String json) {
        try {
            JSONAssert.assertEquals(TemplateUtils.getJsonAsString(JsonUtils.getJsonInputStream(fileName), entityMap.getEntityMap()), json, false);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public static class EntityManagerBuilder {

        private String url;
        private String user;
        private String password;
        private final Set<String> entityPackages = new HashSet<>();

        public EntityManagerBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public EntityManagerBuilder setUser(String user) {
            this.user = user;
            return this;
        }

        public EntityManagerBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public EntityManagerBuilder addEntityPackage(String ... entityPackages) {
            this.entityPackages.addAll(Arrays.asList(entityPackages));
            return this;
        }

        public EntityManager build() {
            return new EntityManager(url, user, password, ReflectionUtils.getEntityMap(entityPackages));
        }

    }

}
