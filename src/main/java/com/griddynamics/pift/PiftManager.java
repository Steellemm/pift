package com.griddynamics.pift;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.ForeignKey;
import com.griddynamics.pift.model.PiftProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
public class PiftManager {

    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    private static final PiftManager INSTANCE = new PiftManager();
    private final PiftProperties piftProperties;

    private PiftManager() {
        PiftProperties props = null;
        try {
            props = PiftManager.getProperties();
            log.info("Properties file has been successfully read");
        } catch (Exception e) {
            log.warn("Problems with properties file", e);
        }
        piftProperties = props;
    }

    public Optional<ForeignKey> getForeignKey(String table, String column) {
        if (piftProperties == null) {
            return Optional.empty();
        }
        if (piftProperties.getTables().containsKey(table) && piftProperties.getTables().get(table)
                .getForeignKeys().containsKey(column)) {
            return Optional.ofNullable(piftProperties.getTables().get(table)
                    .getForeignKeys().get(column));
        }
        return Optional.empty();
    }

    public Optional<Column> getColumn(String table, String column) {
        if (exist(table, column)) {
            return Optional.ofNullable(piftProperties.getTables().get(table)
                    .getColumns().get(column));
        }
        return Optional.empty();
    }

    public boolean exist(String table, String column) {
        if (piftProperties == null) {
            return false;
        }
        return piftProperties.getTables().containsKey(table)
                && (piftProperties.getTables().get(table)
                .getColumns().containsKey(column)
                || piftProperties.getTables().get(table).getForeignKeys().containsKey(column));
    }

    public static PiftProperties getProperties() {
        try(JsonParser jsonParser = MAPPER.createParser(getYaml())) {
            return jsonParser.readValueAs(PiftProperties.class);
        } catch (IOException e) {
            throw new IllegalStateException("Exception during file reading", e);
        }
    }

    private static InputStream getYaml(){
        return PiftManager.class.getResourceAsStream("/pift.yaml");
    }

    public static PiftManager getInstance() {
        return INSTANCE;
    }
}
