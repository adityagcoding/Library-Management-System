package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class.
 * Each call to getConnection() returns a new connection.
 * The caller is responsible for closing it (use try-with-resources).
 */
public class DBConnection {

    // Database URL
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    // MySQL username
    private static final String USER = "root";

    // ============================================================
    // NOTE: Set your MySQL root password below.
    // If your MySQL root has no password, leave it as empty string "".
    // ============================================================
    private static final String DB_PASSWORD = "";

    // Static block to load the JDBC driver once
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            System.err.println("Make sure mysql-connector-j.jar is added to your project build path.");
            e.printStackTrace();
        }
    }

    /**
     * Returns a new database connection.
     * Always use this inside try-with-resources so the connection is closed automatically.
     *
     * @return a new Connection to library_db
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, DB_PASSWORD);
    }
}
