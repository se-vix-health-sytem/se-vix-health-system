package com.nvivx.vixhealthsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @brief Utility class that opens a raw JDBC connection to the PostgreSQL database.
 *
 * Used only by low-level scripts and standalone tools that run outside the Spring
 * container (and therefore cannot rely on the JPA {@code DataSource} bean).
 * All application code should prefer the JPA repositories.
 *
 * <p><b>Note:</b> credentials are hard-coded here for legacy reasons; production
 * deployments must replace them with environment variables or a secrets manager.
 */
public class DBManager {

    // =========================================================
    // CONNECTION CONSTANTS
    // =========================================================

    /** JDBC URL targeting the local PostgreSQL instance. */
    private static final String URL =
            "jdbc:postgresql://localhost:5432/hospital";

    /** Database username — override via environment variable in production. */
    private static final String USER = "postgres";

    /** Database password — override via environment variable in production. */
    private static final String PASSWORD = "password";

    // =========================================================
    // CONNECTION FACTORY
    // =========================================================

    /**
     * Opens and returns a new JDBC {@link Connection}.
     *
     * The caller is responsible for closing the connection (preferably via
     * try-with-resources) to prevent connection leaks.
     *
     * @return a new, open {@link Connection} to the PostgreSQL database
     * @throws SQLException if the driver rejects the URL or credentials
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}