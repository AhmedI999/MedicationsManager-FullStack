package com.simplesolutions.medicinesmanager;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DisplayName("Abstract Unit test class PostgreSQL Container Impl")
public abstract class AbstractTestContainers {
    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway.configure().dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()).load();
        flyway.migrate();
    }

    @AfterEach
    void tearDown() {
        postgreSQLContainer.close();
    }

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("patients-dao-unit-test")
                    .withUsername("ahmed-medicine")
                    .withPassword("fury809");

    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.source.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.data.source.username", postgreSQLContainer::getUsername);
        registry.add("spring.data.source.password", postgreSQLContainer::getPassword);

    }

}
