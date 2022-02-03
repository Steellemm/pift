package com.griddynamics.pift.creator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.griddynamics.pift.Entities.Entity;
import com.griddynamics.pift.model.Column;
import com.griddynamics.pift.model.FieldType;
import com.griddynamics.pift.model.PiftProperties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

class DateFieldCreatorTest {
    DateFieldCreator dateFieldCreator = new DateFieldCreator();
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @SneakyThrows
    void createValue() {
        PiftProperties piftProperties;
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        piftProperties = mapper.readValue(new File("src/test/resources/pift.yaml"), PiftProperties.class);
        Map<String, Column> column = piftProperties.getTables().get("entity").getColumns();
        Date date = (Date) dateFieldCreator.createValue(Entity.class.getDeclaredField("date"), column.get("date"));
        Date dateMin = new Date(format.parse(column.get("date").getCondition().getMin()).getTime());
        Date dateMax = new Date(System.currentTimeMillis());
        Assertions.assertTrue(date.after(dateMin), "Generated date less than dateMin" + dateMin + " < " + date + " < " + dateMax);
        Assertions.assertTrue(date.before(dateMax), "Generated date bigger than dateMax" + dateMin + " < " + date + " < " + dateMax);
    }

    @Test
    void getFieldType() {
        Assertions.assertEquals(FieldType.DATE, dateFieldCreator.getFieldType());
    }
}