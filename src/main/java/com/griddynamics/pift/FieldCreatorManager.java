package com.griddynamics.pift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.javafaker.Faker;
import com.griddynamics.pift.creator.CreatorFunction;
import com.griddynamics.pift.creator.FieldCreator;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.FieldType;
import com.griddynamics.pift.model.PiftProperties;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

import static org.reflections.scanners.Scanners.SubTypes;

@Slf4j
public class FieldCreatorManager {
    private static final Faker faker = new Faker();
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private final Map<Field, CreatorFunction> userCreatorByField = new HashMap<>();
    private final PiftProperties piftProperties;
    private final Reflections reflections = new Reflections("com.griddynamics.pift.creator");
    private final Set<Class<?>> fieldCreatorInheritorsSet = reflections.get(SubTypes.of(FieldCreator.class).asClass());
    private final Map<FieldType, FieldCreator> fieldCreatorMap = new HashMap<>();

    /**
     * Map for autogenerate random values,
     * where key - class of field type
     * value - lambda that generates value for this type
     */
    private final Map<Class<?>, Function<Field, Object>> fieldsMapping = Map.of(
            Long.class, field -> faker.number().randomNumber(),
            String.class, field -> faker.animal().name(),
            Integer.class, field -> faker.number().numberBetween(Integer.MIN_VALUE, Integer.MAX_VALUE),
            BigDecimal.class, field -> new BigDecimal(faker.number().randomNumber()),
            java.sql.Date.class, field -> new Date(faker.date().birthday().getTime()),
            java.sql.Timestamp.class, field -> Timestamp.from(Instant.now()),
            LocalDate.class, field -> faker.date().birthday().toInstant().atZone(ZoneId.of("Europe/Moscow")).toLocalDate(),
            LocalDateTime.class, field -> LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Europe/Moscow"))
    );

    public FieldCreatorManager() {
        try {
            fillFieldCreatorMap();
            piftProperties = MAPPER.readValue(new File("src/test/resources/pift.yaml"), PiftProperties.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception in getYaml method", e);
        }
    }

    public Object createValue(Field field) {
        if (userCreatorByField.containsKey(field)) {
            return userCreatorByField.get(field).apply(field);
        }
        if (existInProperties(field)) {
            return fieldCreatorMap.get(getFromProperties(field).get().getType())
                    .createValue(field, getFromProperties(field).get());
        }
        return fieldsMapping.get(field.getType()).apply(field);
    }

    public Optional<String> getForeignKeyTableName(Field field) {
        String tableName = ReflectionUtils.getTableName(field);
        String columnName = SQLUtils.getColumnName(field);
        if (piftProperties.getTables().containsKey(tableName) && piftProperties.getTables().get(tableName)
                .getForeignKeys().containsKey(columnName)) {
            return Optional.ofNullable(piftProperties.getTables().get(tableName)
                    .getForeignKeys().get(columnName));
        }
        return Optional.empty();
    }

    public void addValueGenerator(Class<?> type, Function<Field, Object> generator) {
        fieldsMapping.put(type, generator);
    }

    public void addValueGenerator(Field field, CreatorFunction creatorFunction) {
        userCreatorByField.put(field, creatorFunction);
    }


    public boolean containsInFieldsMapping(Class<?> type) {
        return fieldsMapping.containsKey(type);
    }

    private Optional<Column> getFromProperties(Field field) {
        if (existInProperties(field)) {
            return Optional.ofNullable(piftProperties.getTables().get(ReflectionUtils.getTableName(field))
                    .getColumns().get(SQLUtils.getColumnName(field)));
        }
        return Optional.empty();
    }

    public boolean existInProperties(Field field) {
        String tableName = ReflectionUtils.getTableName(field);
        String columnName = SQLUtils.getColumnName(field);
        return piftProperties.getTables().containsKey(tableName)
                && (piftProperties.getTables().get(tableName)
                .getColumns().containsKey(columnName)
                || piftProperties.getTables().get(tableName).getForeignKeys().containsKey(columnName));
    }

    private void fillFieldCreatorMap(){
        fieldCreatorInheritorsSet.forEach(clazz -> {
            FieldCreator instance = (FieldCreator) ReflectionUtils.createInstance(clazz);
            fieldCreatorMap.put(instance.getFieldType(), instance);
        });
    }
}
