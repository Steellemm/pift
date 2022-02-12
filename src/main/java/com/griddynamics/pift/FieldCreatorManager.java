package com.griddynamics.pift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.griddynamics.pift.creator.CreatorFunction;
import com.griddynamics.pift.creator.FieldCreator;
import com.griddynamics.pift.fieldsMapping.*;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.ForeignKey;
import com.griddynamics.pift.model.FieldType;
import com.griddynamics.pift.model.PiftProperties;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.reflections.scanners.Scanners.SubTypes;

@Slf4j
public class FieldCreatorManager {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
    private final Map<Field, CreatorFunction> userCreatorByField = new HashMap<>();
    private final PiftProperties piftProperties;
    private final Reflections reflections = new Reflections("com.griddynamics.pift.creator");

    private final Map<FieldType, FieldCreator> fieldCreatorMap = reflections.get(SubTypes.of(FieldCreator.class).asClass())
            .stream().map(x -> (FieldCreator) ReflectionUtils.createInstance(x))
            .collect(Collectors.toMap(FieldCreator::getFieldType, x -> x));

    /**
     * Map for autogenerate random values,
     * where key - class of field type
     * value - lambda that generates value for this type
     */
    private final static Map<Class<?>, FieldValue<?>> fieldsMapping = Stream.of(new Object[][]{
                    {Long.class, new LongFieldValue()},
                    {String.class, new StringFieldValue()},
                    {Integer.class, new IntegerFieldValue()},
                    {BigDecimal.class, new BigDecimalFieldValue()},
                    {java.sql.Date.class, new SqlDateFieldValue()},
                    {Timestamp.class, new TimestampFieldValue()},
                    {LocalDate.class, new LocalDateFieldValue()},
                    {LocalDateTime.class, new LocalDateTimeFieldValue()},
            }
    ).collect(Collectors.toMap(data -> (Class<?>) data[0], data -> (FieldValue<?>) data[1]));

    public FieldCreatorManager() {
        try {
            piftProperties = MAPPER.readValue(new File("src/test/resources/pift.yaml"), PiftProperties.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception in FieldCreatorManager constructor", e);
        }
    }

    public Object getParsedValue(Class<?> type, String value){
        return fieldsMapping.get(type).getValueFromString(value);
    }

    public Object createValue(Field field) {
        if (userCreatorByField.containsKey(field)) {
            return userCreatorByField.get(field).apply(field);
        }
        if (existInProperties(field)) {
            return fieldCreatorMap.get(getFromProperties(field).get().getType())
                    .createValue(field, getFromProperties(field).get());
        }
        return fieldsMapping.get(field.getType()).generate();
    }

    public Optional<ForeignKey> getForeignKey(Field field) {
        String tableName = ReflectionUtils.getTableName(field);
        String columnName = SQLUtils.getColumnName(field);
        if (piftProperties.getTables().containsKey(tableName) && piftProperties.getTables().get(tableName)
                .getForeignKeys().containsKey(columnName)) {
            return Optional.ofNullable(piftProperties.getTables().get(tableName)
                    .getForeignKeys().get(columnName));
        }
        return Optional.empty();
    }

    public void addValueGenerator(Class<?> type, FieldValue<?> generator) {
        fieldsMapping.put(type, generator);
    }

    public void addValueGenerator(Field field, CreatorFunction creatorFunction) {
        userCreatorByField.put(field, creatorFunction);
    }


    public boolean containsInFieldsMapping(Class<?> type) {
        return fieldsMapping.containsKey(type);
    }

    public boolean containsInUserCreatorByField(Field field) {
        return userCreatorByField.containsKey(field);
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
}
