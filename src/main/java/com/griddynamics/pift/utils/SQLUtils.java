package com.griddynamics.pift.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@UtilityClass
public class SQLUtils {

    /**
     * Creates query for select entity from database.
     */
    public static String select(Object entity) {
        String selectQuery = "SELECT * FROM " +
                ReflectionUtils.getTableName(entity.getClass()) +
                " WHERE " + getQueryCondition(entity);
        log.debug(selectQuery);
        return selectQuery;
    }

    /**
     * Creates query for insert entity into database.
     */
    public static String insert(Object entity) {
        Class<?> type = entity.getClass();
        StringBuilder insertQuery =
                new StringBuilder("INSERT INTO ")
                        .append(ReflectionUtils.getTableName(type)).append(" (");
        StringBuilder values = new StringBuilder();

        for (Map.Entry<String, String> entry : ReflectionUtils.getValuesByColumnName(entity).entrySet()) {
            if (values.length() > 0) {
                values.append(",").append(" ");
                insertQuery.append(",").append(" ");
            }
            values.append(entry.getValue());
            insertQuery.append(entry.getKey());
        }
        return insertQuery.append(") values (").append(values).append(")").toString();
    }

    private static String getQueryCondition(Object entity) {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, String> entry : ReflectionUtils.getValuesByColumnName(entity).entrySet()) {
            if (res.length() != 0) {
                res.append(" AND ");
            }
            res.append(entry.getKey());
            res.append(" = ").append(entry.getValue());
        }
        return res.toString();
    }
}
