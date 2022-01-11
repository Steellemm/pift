package com.griddynamics.uspanov.test;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.griddynamics.uspanov.test.SQLUtils.connect;


@Slf4j
public class EntityManager {
    private final List<Object> createdEntitiesList = new ArrayList<>();
    private final String URL;
    private final String USER;
    private final String PASSWORD;

    public EntityManager(String url, String user, String password) {
        URL = url;
        USER = user;
        PASSWORD = password;
    }

    public void flush() {
        createdEntitiesList.forEach(this::saveEntity);
        createdEntitiesList.clear();
    }

    public <T> T create(Class<T> type) {
        return EntityUtils.create(type, createdEntitiesList);
    }

    private void saveEntity(Object entity) {
        connect(URL, USER, PASSWORD, entity);
    }

}
