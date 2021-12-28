package com.griddynamics.uspanov.test;

public class Main {
    public static void main(String[] args) {
        EntityManager entityManager = new EntityManager();
        Entity entity = entityManager.create(Entity.class);
        System.out.println("Age " + entity.getAge());
        System.out.println("Count " + entity.getCount());
        System.out.println("Name " + entity.getName());
        System.out.println("Number " + entity.getNumber());
    }
}
