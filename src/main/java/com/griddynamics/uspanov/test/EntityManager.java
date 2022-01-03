package com.griddynamics.uspanov.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityManager {

    public <T> T create(Class<T> type) {
        return EntityUtils.create(type);
    }

    public void flush() {
        EntityUtils.flush();
    }
}
