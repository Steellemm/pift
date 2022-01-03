package com.griddynamics.uspanov.test;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.griddynamics.uspanov.test.ReflectionUtils.getTableName;


@UtilityClass
@Slf4j
public class EntityUtils {
    private final Faker faker = new Faker();
    private final List<Object> list = new ArrayList<>();
    private static final Sql2o dataSource = new Sql2o
            ("jdbc:postgresql://localhost:5432/test_db", "postgres", "postgres");

    private final Map<Class, Function<Field, Object>> map = Map.of(
            Long.class, field -> faker.number().randomNumber(),
            String.class, field -> faker.animal().name(),
            Integer.class, field -> faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
            BigDecimal.class, field -> new BigDecimal(faker.number().randomNumber())
    );

    public void flush() {
        list.forEach(EntityUtils::saveEntity);
        list.clear();
    }

    public static <T> T create(Class<T> type) {
        T object = createInstance(type);
        try {
            Arrays.stream(type.getDeclaredFields())
                    .filter(x -> !x.isAnnotationPresent(Id.class))
                    .forEach(field -> setField(object, field));
            Arrays.stream(type.getSuperclass().getDeclaredFields())
                    .filter(x -> !x.isAnnotationPresent(Id.class))
                    .forEach(field -> setField(object, field));
        } catch (Exception e) {
            log.error("Exception in create method", e);
        }
        return object;
    }

    private static <T> T createInstance(Class<T> type) {
        try {
            T obj = type.getConstructor().newInstance();
            list.add(obj);
            return obj;
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in createInstance method", e);
        }
    }

    private static void setField(Object obj, Field field) {
        boolean a = field.canAccess(obj);
        try {
            field.setAccessible(true);
            if (map.containsKey(field.getType())) {
                field.set(obj, map.get(field.getType()).apply(field));
            } else if (!field.getAnnotation(OneToOne.class).mappedBy().equals("")) {
                field.set(obj, list.get(list.size() - 2));
            } else {
                field.set(obj, create(field.getType()));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in setField method", e);
        } finally {
            field.setAccessible(a);
        }
    }

    private void saveEntity(Object entity) {
        Class<?> type = entity.getClass();
        String tableName = getTableName(type);
        String insertQuery = createInsertQuery(tableName, type);

        try (Connection con = dataSource.open()) {
            log.info(insertQuery);
            con.setRollbackOnException(false);
            con.createQuery(insertQuery, true)
                    .bind(entity)
                    .executeUpdate();
        }
    }

    private String createInsertQuery(String tableName, Class<?> type) {
        StringBuilder insertQuery = new StringBuilder("insert into " + tableName + " (" + Arrays
                .stream(type.getDeclaredFields())
                .filter(x ->
                        !x.isAnnotationPresent(Transient.class) &&
                                !x.isAnnotationPresent(Id.class) &&
                                !x.isAnnotationPresent(OneToOne.class))
                .map(ReflectionUtils::getColumnName)
                .collect(Collectors.joining(",")) + ") values (");

        Arrays.stream(type.getDeclaredFields())
                .filter(x ->
                        !x.isAnnotationPresent(Transient.class) &&
                                !x.isAnnotationPresent(Id.class) &&
                                !x.isAnnotationPresent(OneToOne.class))
                .map(ReflectionUtils::getColumnName)
                .forEach(x -> insertQuery.append(":").append(x).append(", "));

        insertQuery.setCharAt(insertQuery.lastIndexOf(","), ')');

        return insertQuery.toString();
    }
}
