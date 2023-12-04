package com.simplesolutions.medicinesmanager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test unit class for testing PostgreSQLContainer connection")
public class TestContainersContainers extends AbstractTestContainers {
    @Test
    @DisplayName("Test if postgresSql testContainer is working correctly")
    void canStartPostgresDB() {
        Assertions.assertThat(postgreSQLContainer.isRunning()).isTrue();
        Assertions.assertThat(postgreSQLContainer.isCreated()).isTrue();
    }

//    @Test
//    @DisplayName("Test if db migration with flyway can be applied")
//    void canMigrateWithFlyway(){
//        Flyway flyway = Flyway.configure().dataSource(
//                postgreSQLContainer.getJdbcUrl(),
//                postgreSQLContainer.getUsername(),
//                postgreSQLContainer.getPassword()).load();
//        flyway.migrate();
//        System.out.println("Debug here to test");
//    }

    // FOR How to test JDBC Data service see repo amigoscode! Lesson 135 onwards to
}
