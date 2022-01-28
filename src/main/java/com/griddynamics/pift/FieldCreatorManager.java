package com.griddynamics.pift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.javafaker.Faker;
import com.griddynamics.pift.creator.CreatorFunction;
import com.griddynamics.pift.creator.FieldCreator;
import com.griddynamics.pift.creator.StringFieldCreator;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.FieldType;
import com.griddynamics.pift.model.PiftProperties;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class FieldCreatorManager {
    private static final Faker faker = new Faker();
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private static final Map<Field, CreatorFunction> userCreatorByField = new HashMap<>();
    private static PiftProperties piftProperties;

    private final Map<FieldType, FieldCreator> fieldCreatorMap = Map.of(
            FieldType.STRING, new StringFieldCreator()
    );

    /**
     * Map for autogenerate random values,
     * where key - class of field type
     * value - lambda that generates value for this type
     */
    private static final Map<Class<?>, Function<Field, Object>> fieldsMapping = Map.of(
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
        setYaml();
    }

    private static void setYaml() {
        try {
            piftProperties = MAPPER.readValue(new File("pift.yaml"), PiftProperties.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception in getYaml method", e);
        }
    }

    public Object createValue(Field field, List<Object> createdEntitiesList) {
        if (userCreatorByField.containsKey(field)) {
            return userCreatorByField.get(field).apply(field);
        }
        if (existInProperties(field).isPresent()) {
            if (existInForeignKeys(field).isPresent()){
                return createdEntitiesList.stream()
                        .filter(obj -> ReflectionUtils.getTableName(obj.getClass())
                                .equals(existInForeignKeys(field).orElse("")))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("FK object has not been created yet"));
            }
            return fieldCreatorMap.get(existInProperties(field).get().getType())
                    .createValue(field, existInProperties(field).get());
        }
        return fieldsMapping.get(field.getType()).apply(field);
    }

    private Optional<Column> existInProperties(Field field) {
        if (piftProperties.getTables().containsKey(ReflectionUtils.getTableName(field))
                && piftProperties.getTables().get(ReflectionUtils.getTableName(field))
                .getColumns().containsKey(SQLUtils.getColumnName(field))) {
            return Optional.ofNullable(piftProperties.getTables().get(ReflectionUtils.getTableName(field))
                    .getColumns().get(SQLUtils.getColumnName(field)));
        }
        return Optional.empty();
    }

    private Optional<String> existInForeignKeys(Field field) {
        if (piftProperties.getTables().containsKey(ReflectionUtils.getTableName(field))
                && piftProperties.getTables().get(ReflectionUtils.getTableName(field))
                .getForeignKeys().containsKey(SQLUtils.getColumnName(field))) {
            return Optional.ofNullable(piftProperties.getTables().get(ReflectionUtils.getTableName(field))
                    .getForeignKeys().get(SQLUtils.getColumnName(field)));
        }
        return Optional.empty();
    }

    public static void addValueGenerator(Class<?> type, Function<Field, Object> generator) {
        fieldsMapping.put(type, generator);
    }

    public static void addValueGenerator(Field field, CreatorFunction creatorFunction) {
        userCreatorByField.put(field, creatorFunction);
    }

    public static boolean contains(Class<?> type) {
        return fieldsMapping.containsKey(type);
    }
}
