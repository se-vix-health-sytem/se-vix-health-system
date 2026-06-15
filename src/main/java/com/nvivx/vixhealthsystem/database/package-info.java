/**
 * Low-level database access utilities.
 *
 * {@link com.nvivx.vixhealthsystem.database.DBManager} provides a plain JDBC
 * connection for operations that fall outside of the JPA layer — mostly used
 * during initial development and for diagnostic queries.
 *
 * For all regular persistence, prefer the Spring Data repositories in
 * {@code com.nvivx.vixhealthsystem.repository}.
 *
 * Main curator: Viviana Fraccaroli
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.database;
