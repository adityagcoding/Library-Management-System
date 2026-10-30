package ui;

import dao.BookDAO;
import model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Book Management window.
 * Allows admin to Add, Update, Delete, Search, and View all books.
 */
public class BookManagement extends JFrame {
    private JTextField txtTitle, txtAuthor, txtCategory, txtQuantity, txtSearch;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;
    private int selectedBookId = -1;

    public BookManagement() {
        bookDAO = new BookDAO();

        setTitle("Book Management");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Colors
        Color primaryColor = new Color(44, 62, 80);
        Color backgroundColor = new Color(236, 240, 241);
        Color accentColor = new Color(52, 152, 219);
        Color successColor = new Color(39, 174, 96);
        Color dangerColor = new Color(231, 76, 60);
        Color white = Color.WHITE;

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === Top Panel: Form + Buttons ===
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(backgroundColor);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));

        formPanel.add(new JLabel("Title:"));
        txtTitle = new JTextField();
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Author:"));
        txtAuthor = new JTextField();
        formPanel.add(txtAuthor);

        formPanel.add(new JLabel("Category:"));
        txtCategory = new JTextField();
        formPanel.add(txtCategory);

        formPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        formPanel.add(txtQuantity);

        topPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(backgroundColor);

        JButton btnAdd = createButton("Add", successColor, white);
        JButton btnUpdate = createButton("Update", accentColor, white);
        JButton btnDelete = createButton("Delete", dangerColor, white);
        JButton btnClear = createButton("Clear", primaryColor, white);

        txtSearch = new JTextField(15);
        JButton btnSearch = createButton("Search", accentColor, white);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(new JLabel("  Search:"));
        buttonPanel.add(txtSearch);
        buttonPanel.add(btnSearch);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // === Center: Table ===
        String[] columns = {"ID", "Title", "Author", "Category", "Quantity", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bookTable.setFont(new Font("Arial", Font.PLAIN, 14));
        bookTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // === Event Listeners ===

        // When a table row is clicked, populate the form fields
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                int row = bookTable.getSelectedRow();
                selectedBookId = (int) tableModel.getValueAt(row, 0);
                txtTitle.setText(tableModel.getValueAt(row, 1).toString());
                txtAuthor.setText(tableModel.getValueAt(row, 2).toString());
                txtCategory.setText(tableModel.getValueAt(row, 3).toString());
                txtQuantity.setText(tableModel.getValueAt(row, 4).toString());
            }
        });

        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchBooks());

        // Load all books when the window opens
        refreshTable();
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        return btn;
    }

    private void clearForm() {
        txtTitle.setText("");
        txtAuthor.setText("");
        txtCategory.setText("");
        txtQuantity.setText("");
        txtSearch.setText("");
        selectedBookId = -1;
        bookTable.clearSelection();
    }

    private void refreshTable() {
        loadTableData(bookDAO.getAllBooks());
    }

    private void searchBooks() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
        } else {
            loadTableData(bookDAO.searchBooks(keyword));
        }
    }

    private void loadTableData(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getBookId(), b.getTitle(), b.getAuthor(),
                b.getCategory(), b.getQuantity(), b.getAvailable()
            });
        }
    }

    private void addBook() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String category = txtCategory.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        // Validation
        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // For a new book, available = quantity
            Book book = new Book(0, title, author, category, quantity, quantity);
            if (bookDAO.addBook(book)) {
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        if (selectedBookId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String category = txtCategory.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the current book to preserve the available count correctly
            Book currentBook = bookDAO.getBookById(selectedBookId);
            int currentIssued = currentBook.getQuantity() - currentBook.getAvailable();
            int newAvailable = quantity - currentIssued;

            if (newAvailable < 0) {
                JOptionPane.showMessageDialog(this,
                    "New quantity (" + quantity + ") cannot be less than currently issued books (" + currentIssued + ").",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Book book = new Book(selectedBookId, title, author, category, quantity, newAvailable);
            if (bookDAO.updateBook(book)) {
                JOptionPane.showMessageDialog(this, "Book updated successfully!");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        if (selectedBookId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(selectedBookId)) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Cannot delete this book. It may have active issue records.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
