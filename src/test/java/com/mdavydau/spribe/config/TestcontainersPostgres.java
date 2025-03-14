package com.mdavydau.spribe.config;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
public class TestcontainersPostgres {
    @Container
    static PostgreSQLContainer postgreSQLContainer =
            (PostgreSQLContainer)
                    new PostgreSQLContainer("postgres:latest")
                            .withDatabaseName("spribe")
                            .withReuse(true);
}
