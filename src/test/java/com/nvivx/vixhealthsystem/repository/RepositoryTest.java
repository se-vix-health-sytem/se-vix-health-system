package com.nvivx.vixhealthsystem.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class RepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldConnectToDatabase() throws Exception {

        try (Connection connection = dataSource.getConnection()) {

            assertNotNull(connection);
            assertFalse(connection.isClosed());

            System.out.println(
                    "Connected to: "
                            + connection.getMetaData().getURL()
            );
        }
    }
}