package dao;

import model.Member;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Member operations.
 * Handles all database CRUD operations for the Members table.
 */
public class MemberDAO {

    /**
     * Adds a new member to the database.
     * If membershipDate is not provided, uses CURDATE().
     */
    public boolean addMember(Member member) {
        String query = "INSERT INTO Members (name, contact, email, membership_date) VALUES (?, ?, ?, CURDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getContact());
            pstmt.setString(3, member.getEmail());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing member in the database.
     */
    public boolean updateMember(Member member) {
        String query = "UPDATE Members SET name = ?, contact = ?, email = ? WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getContact());
            pstmt.setString(3, member.getEmail());
            pstmt.setInt(4, member.getMemberId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a member has any active (not returned) issues.
     */
    public boolean hasActiveIssues(int memberId) {
        String query = "SELECT COUNT(*) FROM Issues WHERE member_id = ? AND status = 'Issued'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking active issues for member: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a member. Returns false if the member has active issues.
     */
    public boolean deleteMember(int memberId) {
        if (hasActiveIssues(memberId)) {
            System.err.println("Cannot delete member: Member has active issues.");
            return false;
        }

        String query = "DELETE FROM Members WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a member by ID.
     */
    public Member getMemberById(int memberId) {
        String query = "SELECT * FROM Members WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractMember(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving member by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all members from the database.
     */
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM Members";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                members.add(extractMember(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all members: " + e.getMessage());
        }
        return members;
    }

    /**
     * Searches members by name, contact, or email.
     */
    public List<Member> searchMembers(String keyword) {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM Members WHERE name LIKE ? OR contact LIKE ? OR email LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    members.add(extractMember(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching members: " + e.getMessage());
        }
        return members;
    }

    /**
     * Helper method to extract a Member from a ResultSet row.
     * Converts SQL Date to String for the model.
     */
    private Member extractMember(ResultSet rs) throws SQLException {
        String dateStr = "";
        if (rs.getDate("membership_date") != null) {
            dateStr = rs.getDate("membership_date").toString();
        }
        return new Member(
                rs.getInt("member_id"),
                rs.getString("name"),
                rs.getString("contact"),
                rs.getString("email"),
                dateStr
        );
    }
}
