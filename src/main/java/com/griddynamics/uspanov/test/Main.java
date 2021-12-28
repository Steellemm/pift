package com.griddynamics.uspanov.test;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        EntityManager entityManager = new EntityManager();
        Entity entity = entityManager.create(Entity.class);
        System.out.println(entity.getAge() + " "  + entity.getName());
    }
}
