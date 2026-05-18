package com.library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages MySQL connections for the Library Management System.
 *
 * Configuration: edit the constants below to match your MySQL instance.
 * Every caller MUST obtain a Connection via getConnection() and close it
 * inside a try-with-resources block to prevent connection leaks.
 *
 * Example:
 *   try (Connection conn = DatabaseManager.getConnection();
 *        PreparedStatement ps = conn.prepareStatement(sql)) {
 *       ...
 *   }
 */
public final class DatabaseManager {

    // ── Connection settings — edit these to match your MySQL setup ───────────
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "library_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "Subhechha@27";          // ← change this

    private static final String URL =
            "jdbc:mysql://localhost:3306/library_db"
            + "?useSSL=false"
            + "&serverTimezone=UTC"
            + "&allowPublicKeyRetrieval=true"
            + "&characterEncoding=utf8";

    // ── Prevent instantiation ────────────────────────────────────────────────
    private DatabaseManager() {}

    /**
     * Returns a new JDBC connection.
     * The caller is responsible for closing it (try-with-resources).
     *
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Convenience: tests the connection and prints the DB product name.
     * Called once at application startup.
     */
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("[DB] Connected to: "
                    + conn.getMetaData().getDatabaseProductName()
                    + " " + conn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("[DB] Connection FAILED: " + e.getMessage());
            throw new RuntimeException("Cannot connect to MySQL. Check schema.sql was run and credentials are correct.", e);
        }
    }
}
