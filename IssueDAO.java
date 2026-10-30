package dao;

import model.Issue;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Issue/Return operations.
 * Handles book issuing and returning with database transactions.
 */
public class IssueDAO {

    /**
     * Issues a book to a member.
     * Uses a database transaction to ensure atomicity:
     * 1. Decrease available count (fails if available = 0)
     * 2. Insert issue record
     */
    public boolean issueBook(int bookId, int memberId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Decrease available count (atomic check for available > 0)
            String updateBookQuery = "UPDATE Books SET available = available - 1 WHERE book_id = ? AND available > 0";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookQuery)) {
                updateStmt.setInt(1, bookId);
                if (updateStmt.executeUpdate() <= 0) {
                    // Book not available
                    conn.rollback();
                    return false;
                }
            }

            // Step 2: Insert issue record
            String insertIssueQuery = "INSERT INTO Issues (book_id, member_id, issue_date, status) VALUES (?, ?, CURDATE(), 'Issued')";
            try (PreparedStatement pstmt = conn.prepareStatement(insertIssueQuery)) {
                pstmt.setInt(1, bookId);
                pstmt.setInt(2, memberId);
                if (pstmt.executeUpdate() <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error issuing book: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error closing connection: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Returns a book by issue ID.
     * Uses a database transaction:
     * 1. Find the book_id from the issue record
     * 2. Update return_date and status to 'Completed'
     * 3. Increase available count
     */
    public boolean returnBook(int issueId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Get book_id from the issue record
            int bookId = -1;
            String selectQuery = "SELECT book_id FROM Issues WHERE issue_id = ? AND status = 'Issued'";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, issueId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        bookId = rs.getInt("book_id");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Step 2: Update issue record
            String updateQuery = "UPDATE Issues SET return_date = CURDATE(), status = 'Completed' WHERE issue_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, issueId);
                if (updateStmt.executeUpdate() <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Step 3: Increase available count
            String updateBookQuery = "UPDATE Books SET available = available + 1 WHERE book_id = ? AND available < quantity";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookQuery)) {
                updateStmt.setInt(1, bookId);
                if (updateStmt.executeUpdate() <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error closing connection: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Retrieves all issue records.
     */
    public List<Issue> getAllIssues() {
        List<Issue> issues = new ArrayList<>();
        String query = "SELECT * FROM Issues";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                issues.add(extractIssue(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all issues: " + e.getMessage());
        }
        return issues;
    }

    /**
     * Retrieves only active (status = 'Issued') issue records.
     */
    public List<Issue> getActiveIssues() {
        List<Issue> issues = new ArrayList<>();
        String query = "SELECT * FROM Issues WHERE status = 'Issued'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                issues.add(extractIssue(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving active issues: " + e.getMessage());
        }
        return issues;
    }

    /**
     * Retrieves issues by member ID.
     */
    public List<Issue> getIssuesByMember(int memberId) {
        List<Issue> issues = new ArrayList<>();
        String query = "SELECT * FROM Issues WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    issues.add(extractIssue(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving issues by member: " + e.getMessage());
        }
        return issues;
    }

    /**
     * Retrieves issues by book ID.
     */
    public List<Issue> getIssuesByBook(int bookId) {
        List<Issue> issues = new ArrayList<>();
        String query = "SELECT * FROM Issues WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    issues.add(extractIssue(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving issues by book: " + e.getMessage());
        }
        return issues;
    }

    /**
     * Helper method to extract an Issue from a ResultSet row.
     * Converts SQL Dates to Strings for the model.
     */
    private Issue extractIssue(ResultSet rs) throws SQLException {
        String issueDateStr = "";
        if (rs.getDate("issue_date") != null) {
            issueDateStr = rs.getDate("issue_date").toString();
        }
        String returnDateStr = "";
        if (rs.getDate("return_date") != null) {
            returnDateStr = rs.getDate("return_date").toString();
        }
        return new Issue(
                rs.getInt("issue_id"),
                rs.getInt("book_id"),
                rs.getInt("member_id"),
                issueDateStr,
                returnDateStr,
                rs.getString("status")
        );
    }
}
