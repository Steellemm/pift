package com.griddynamics.uspanov.test;

public class Main {
    public static void main(String[] args) {
        EntityManager entityManager = new EntityManager();
        Entity entity = entityManager.create(Entity.class);
        entityManager.flush();
    }
}
