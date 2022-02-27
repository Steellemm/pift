package com.griddynamics.pift;

import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.ForeignKey;
import com.griddynamics.pift.model.PiftProperties;
import com.griddynamics.pift.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
public class PiftPropertiesManager {

    private final PiftProperties piftProperties;

    public PiftPropertiesManager() {
        PiftProperties props = null;
        try {
            props = JsonUtils.getProperties();
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

    public Path getPathToTemplate(String fileName) {
        if (fileName.contains("/")) {
            throw new IllegalArgumentException("Invalid filename: " + fileName);
        }
        String fileNameWithExt = fileName.contains(".")? fileName : fileName + ".jsont";
        return Paths.get(piftProperties.getTemplate().getPath(), fileNameWithExt);
    }
}
