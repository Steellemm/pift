# **Introduction**
**PIFT** is a library for integration testing applications with relational database.

# **Example of using**

```java
import java.util.Map;

class Example {
    @Test
    void test() {
        entityManager.create(Table1.class);
        entityManager.create(Table2.class, Map.of("field1", "value1"));
        entityManager.create("table1");
        entityManager.create("table2", "table2Alias");
        entityManager.flush();
    }
}
```

# **Configuration**
**EntityManager** is the main class for interacting with the library. 
For creating EntityManger builder exists. Example:

```java
class Example {
    EntityManager entityManager = EntityManager.builder()
            .setUrl("jdbc:h2:mem:myDb")
            .setPassword("password")
            .setUser("user")
            .addEntityPackage("com.example.entities")
            .build();
}
```
Beyond the database information you could pass packages with your entities. 
It provides an opportunity to create entity by name instead of creating by type.

The class provides several methods: 
* methods for creating entities
* methods for selecting from database entities that have been inserted previously
* methods for testing
* flush

***Create methods*** create an entity object
based on a table in the database and fills in the required fields with random values. 
The class of the entity to create is passed in the parameter.

The ***flush*** method uploads entities created using the ***create*** method to the testing database.

