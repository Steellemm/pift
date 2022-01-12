package com.griddynamics.pift;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class EntityManager {
    private final List<Object> createdEntitiesList = new ArrayList<>();
    private final String URL;
    private final String USER;
    private final String PASSWORD;

    public void flush() {
        createdEntitiesList.forEach(this::saveEntity);
        createdEntitiesList.clear();
    }

    public <T> T create(Class<T> type) {
        return EntityUtils.create(type, createdEntitiesList);
    }

    private void saveEntity(Object entity) {
        SQLUtils.connect(URL, USER, PASSWORD, entity);
    }

}
