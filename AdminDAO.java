package dao;

import model.Admin;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Admin operations.
 * Handles admin login validation.
 */
public class AdminDAO {

    /**
     * Validates admin login credentials against the database.
     * @param username the admin username
     * @param password the admin password
     * @return true if credentials are valid
     */
    public boolean validateLogin(String username, String password) {
        String query = "SELECT COUNT(*) FROM Admin WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating login: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves an admin by username.
     * @param username the admin username
     * @return Admin object or null if not found
     */
    public Admin getAdmin(String username) {
        String query = "SELECT * FROM Admin WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("admin_id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving admin: " + e.getMessage());
        }
        return null;
    }
}
