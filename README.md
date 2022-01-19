# **Introduction**
**PIFT** is a simple library for integration testing of services that work with databases.

# **Configuration**
**EntityManager** is the main class for interacting with the library. The constructor accepts url, username, password to connect to the database.
The class provides two methods - create and flush.

The ***create*** method creates an entity object
based on a table in the database and fills in the required fields with random values. 
The class of the entity to create is passed in the parameter.
The ***flush*** method uploads entities created using the ***create*** method to the testing database.

Entities created using the ***create*** method are stored in the ***createdEntitiesList***, which is cleared after each ***flush*** method call.

