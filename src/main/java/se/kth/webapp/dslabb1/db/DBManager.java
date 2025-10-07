package se.kth.webapp.dslabb1.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager.
 * Creates new connection for each operation in a current session.
 */
public class DBManager implements AutoCloseable {

    private static final String DB_URL = "jdbc:mysql://78.72.148.32:3306/webshop";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

    private Connection conn;
    private boolean transactionActive = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Updated MySQL driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found", e);
        }
    }

    public DBManager() {
        try {
            this.conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (this.conn == null || this.conn.isClosed()) {
                throw new SQLException("Failed to create database connection");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to establish database connection: " + e);
        }
    }

    /**
     * Creates a new DBManager instance and opens a connection.
     */
    public static DBManager open() {
        return new DBManager();
    }

    /**
     * Starts a database transaction.
     */
    public void startTransaction() {
        try {
            conn.setAutoCommit(false);
            transactionActive = true;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to start transaction: " + e);
        }
    }

    /**
     * Commits the current transaction.
     */
    public void commit() {
        try {
            if (transactionActive) {
                conn.commit();
                conn.setAutoCommit(true);
                transactionActive = false;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to commit transaction: " + e);
        }
    }

    /**
     * Rolls back the current transaction.
     */
    public void rollback() {
        try {
            if (transactionActive) {
                conn.rollback();
                conn.setAutoCommit(true);
                transactionActive = false;
            }
        } catch (SQLException e) {
            System.err.println("Rollback failed: " + e.getMessage());
        }
    }


    /**
     * Fetches a connection to the database for a method requesting it.
     *
     * @return the connection.
     * @throws SQLException if connection fails to be established.
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Tries to connect to the database.
     *
     * @return whether connection was successful.
     */
    public static boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves generic info of the database connected to.
     */
    public static String getDatabaseInfo() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            return "Connected to: " + conn.getMetaData().getURL() +
                    " | Driver: " + conn.getMetaData().getDriverName();
        } catch (SQLException e) {
            return "Database connection failed: " + e.getMessage();
        }
    }

    /**
     * Closes the underlying connection.
     */
    @Override
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }
}