package com.griddynamics.pift;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EntityMap {

    private final LinkedHashMap<String, Object> createdEntitiesMap = new LinkedHashMap<>();
    private final List<String> notFlushedEntityIds = new ArrayList<>();

    public String add(Object entity) {
        return add(entity, getEntityId(entity.getClass()));
    }

    public String add(Object entity, String entityId) {
        createdEntitiesMap.put(entityId, entity);
        notFlushedEntityIds.add(entityId);
        return entityId;
    }

    public <T> T get(String entityId) {
        return (T) createdEntitiesMap.get(entityId);
    }

    public Optional<Object> getLast(Predicate<Object> predicate) {
        Object appropriateEntity = null;
        for (Object entity: createdEntitiesMap.values()) {
            if (predicate.test(entity)) {
                appropriateEntity = entity;
            }
        }
        return Optional.ofNullable(appropriateEntity);
    }

    public Stream<Object> getNotFlushedEntities() {
        return notFlushedEntityIds.stream().map(createdEntitiesMap::get);
    }

    public void flush() {
        notFlushedEntityIds.clear();
    }

    private String getEntityId(Class<?> type) {
        String str = type.getSimpleName();
        int counter = 1;
        while (createdEntitiesMap.containsKey(str + counter)) {
            counter++;
        }
        return str + counter;
    }

}
