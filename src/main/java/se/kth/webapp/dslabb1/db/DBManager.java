package se.kth.webapp.dslabb1.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager
 * Creates new connection for each operation (thread-safe, transaction-safe)
 */
public class DBManager {

    private static final String DB_URL = "jdbc:mysql://78.72.148.32:3306/webshop";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Updated MySQL driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (conn == null || conn.isClosed()) {
                throw new SQLException("Failed to create database connection");
            }
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;  // Re-throw for caller to handle
        }
    }

    /**
     * Test database connectivity
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get database info for debugging
     */
    public static String getDatabaseInfo() {
        try (Connection conn = getConnection()) {
            return "Connected to: " + conn.getMetaData().getURL() +
                    " | Driver: " + conn.getMetaData().getDriverName();
        } catch (SQLException e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}