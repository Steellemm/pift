package com.griddynamics.uspanov.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.sql2o.Sql2o;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.griddynamics.uspanov.test.ReflectionUtils.*;

@Slf4j
public class SQLUtils {
    public static void connect(String url, String user, String password, Object entity) {

        String insertQuery = createQueryForInsert(entity);

        log.debug(insertQuery);

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(insertQuery);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in connect method", e);
        }
    }

    private static String createQueryForInsert(Object entity) {
        Class<?> type = entity.getClass();
        StringBuilder insertQuery =
                new StringBuilder("INSERT INTO ")
                        .append(getTableName(type)).append(" (");
        StringBuilder values = new StringBuilder();

        getColumnFields(entity).forEach(field -> {
                    if (values.length() > 0) {
                        values.append(",").append(" ");
                        insertQuery.append(",").append(" ");
                    }
                    if (field.isAnnotationPresent(JoinColumn.class)){
                        values.append(readField(Arrays.stream(getFieldValue(field, entity).getClass()
                                        .getDeclaredFields()).filter(x -> x.isAnnotationPresent(Id.class))
                                        .findFirst().get(), getFieldValue(field, entity)));
                    }
                    else values.append(readField(field, entity));
                    insertQuery.append(getColumnName(field));
                });
        return insertQuery.append(") values (").append(values).append(")").toString();
    }

    private static String getColumnName(Field field) {
        if (field.isAnnotationPresent(JoinColumn.class)) {
            return field.getAnnotation(JoinColumn.class).name();
        }
        if (field.isAnnotationPresent(Column.class) &&
                !field.getAnnotation(Column.class).name().isBlank()) {
            return field.getAnnotation(Column.class).name();
        }
        return field.getName();
    }
}
