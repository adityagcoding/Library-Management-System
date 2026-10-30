package dao;

import model.Book;
import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Book operations.
 * Handles all database CRUD operations for the Books table.
 */
public class BookDAO {

    /**
     * Adds a new book to the database.
     */
    public boolean addBook(Book book) {
        String query = "INSERT INTO Books (title, author, category, quantity, available) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCategory());
            pstmt.setInt(4, book.getQuantity());
            pstmt.setInt(5, book.getAvailable());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing book in the database.
     */
    public boolean updateBook(Book book) {
        String query = "UPDATE Books SET title = ?, author = ?, category = ?, quantity = ?, available = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCategory());
            pstmt.setInt(4, book.getQuantity());
            pstmt.setInt(5, book.getAvailable());
            pstmt.setInt(6, book.getBookId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a book has any active (not returned) issues.
     */
    public boolean hasActiveIssues(int bookId) {
        String query = "SELECT COUNT(*) FROM Issues WHERE book_id = ? AND status = 'Issued'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking active issues for book: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a book. Returns false if the book has active issues.
     */
    public boolean deleteBook(int bookId) {
        if (hasActiveIssues(bookId)) {
            System.err.println("Cannot delete book: Book has active issues.");
            return false;
        }

        String query = "DELETE FROM Books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a book by its ID.
     */
    public Book getBookById(int bookId) {
        String query = "SELECT * FROM Books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractBook(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving book by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all books from the database.
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                books.add(extractBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all books: " + e.getMessage());
        }
        return books;
    }

    /**
     * Searches books by title, author, or category using keyword.
     */
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        return books;
    }

    /**
     * Decreases the available count of a book by 1.
     */
    public boolean decreaseAvailable(int bookId) {
        String query = "UPDATE Books SET available = available - 1 WHERE book_id = ? AND available > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error decreasing available count: " + e.getMessage());
            return false;
        }
    }

    /**
     * Increases the available count of a book by 1.
     */
    public boolean increaseAvailable(int bookId) {
        String query = "UPDATE Books SET available = available + 1 WHERE book_id = ? AND available < quantity";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error increasing available count: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to extract a Book object from a ResultSet row.
     */
    private Book extractBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getInt("available")
        );
    }
}
