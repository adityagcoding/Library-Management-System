package ui;

import dao.BookDAO;
import dao.IssueDAO;
import dao.MemberDAO;
import model.Book;
import model.Issue;
import model.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Issue & Return window.
 * Tab 1: Issue a book to a member.
 * Tab 2: Return a previously issued book.
 */
public class IssueReturn extends JFrame {
    private JTabbedPane tabbedPane;
    private JComboBox<String> memberCombo;
    private JComboBox<String> bookCombo;
    private JTable issueTable;
    private DefaultTableModel issueTableModel;

    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    private IssueDAO issueDAO;

    private List<Member> membersList;
    private List<Book> booksList;

    public IssueReturn() {
        bookDAO = new BookDAO();
        memberDAO = new MemberDAO();
        issueDAO = new IssueDAO();

        setTitle("Issue & Return Books");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Colors
        Color primaryColor = new Color(44, 62, 80);
        Color backgroundColor = new Color(236, 240, 241);
        Color accentColor = new Color(52, 152, 219);
        Color successColor = new Color(39, 174, 96);
        Color white = Color.WHITE;

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // ============ TAB 1: Issue Book ============
        JPanel issuePanel = new JPanel(new BorderLayout(20, 20));
        issuePanel.setBackground(backgroundColor);
        issuePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel issueFormPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        issueFormPanel.setBackground(backgroundColor);

        JLabel lblMember = new JLabel("Select Member:");
        lblMember.setFont(new Font("Arial", Font.BOLD, 16));
        memberCombo = new JComboBox<>();
        memberCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblBook = new JLabel("Select Book:");
        lblBook.setFont(new Font("Arial", Font.BOLD, 16));
        bookCombo = new JComboBox<>();
        bookCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnIssue = new JButton("Issue Book");
        btnIssue.setFont(new Font("Arial", Font.BOLD, 16));
        btnIssue.setBackground(successColor);
        btnIssue.setForeground(white);
        btnIssue.setFocusPainted(false);

        issueFormPanel.add(lblMember);
        issueFormPanel.add(memberCombo);
        issueFormPanel.add(lblBook);
        issueFormPanel.add(bookCombo);
        issueFormPanel.add(new JLabel("")); // spacer
        issueFormPanel.add(btnIssue);

        issuePanel.add(issueFormPanel, BorderLayout.NORTH);

        // Active issues table shown below the issue form
        String[] issueColumns = {"Issue ID", "Book ID", "Member ID", "Issue Date", "Status"};
        DefaultTableModel activeModel = new DefaultTableModel(issueColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable activeTable = new JTable(activeModel);
        activeTable.setFont(new Font("Arial", Font.PLAIN, 13));
        activeTable.setRowHeight(22);
        JScrollPane activeScroll = new JScrollPane(activeTable);
        activeScroll.setBorder(BorderFactory.createTitledBorder("Currently Issued Books"));
        issuePanel.add(activeScroll, BorderLayout.CENTER);

        tabbedPane.addTab("Issue Book", issuePanel);

        // ============ TAB 2: Return Book ============
        JPanel returnPanel = new JPanel(new BorderLayout(10, 10));
        returnPanel.setBackground(backgroundColor);
        returnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] returnColumns = {"Issue ID", "Book ID", "Member ID", "Issue Date", "Status"};
        issueTableModel = new DefaultTableModel(returnColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        issueTable = new JTable(issueTableModel);
        issueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issueTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        issueTable.setFont(new Font("Arial", Font.PLAIN, 14));
        issueTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(issueTable);
        returnPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel returnBottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        returnBottomPanel.setBackground(backgroundColor);

        JButton btnReturn = new JButton("Return Selected Book");
        btnReturn.setFont(new Font("Arial", Font.BOLD, 14));
        btnReturn.setBackground(accentColor);
        btnReturn.setForeground(white);
        btnReturn.setFocusPainted(false);

        JButton btnRefresh = new JButton("Refresh List");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefresh.setBackground(primaryColor);
        btnRefresh.setForeground(white);
        btnRefresh.setFocusPainted(false);

        returnBottomPanel.add(btnReturn);
        returnBottomPanel.add(btnRefresh);
        returnPanel.add(returnBottomPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Return Book", returnPanel);

        add(tabbedPane);

        // ============ Load Data ============
        loadComboBoxData();
        loadActiveIssues();
        loadActiveIssuesIntoTable(activeModel);

        // ============ Event Listeners ============
        btnIssue.addActionListener(e -> {
            issueBook();
            loadActiveIssuesIntoTable(activeModel); // refresh after issue
        });
        btnReturn.addActionListener(e -> returnBook());
        btnRefresh.addActionListener(e -> loadActiveIssues());

        // Refresh data when switching tabs
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                loadComboBoxData();
                loadActiveIssuesIntoTable(activeModel);
            } else {
                loadActiveIssues();
            }
        });
    }

    /**
     * Allows Dashboard to set which tab is initially shown.
     * 0 = Issue Book, 1 = Return Book
     */
    public void setTab(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    /** Loads members and books into the combo boxes. */
    private void loadComboBoxData() {
        memberCombo.removeAllItems();
        bookCombo.removeAllItems();

        membersList = memberDAO.getAllMembers();
        for (Member m : membersList) {
            memberCombo.addItem(m.getMemberId() + " - " + m.getName());
        }

        booksList = bookDAO.getAllBooks();
        for (Book b : booksList) {
            bookCombo.addItem(b.getBookId() + " - " + b.getTitle() + " (Available: " + b.getAvailable() + ")");
        }
    }

    /** Loads active issues into the Return tab table. */
    private void loadActiveIssues() {
        issueTableModel.setRowCount(0);
        List<Issue> issues = issueDAO.getActiveIssues();
        for (Issue i : issues) {
            issueTableModel.addRow(new Object[]{
                i.getIssueId(), i.getBookId(), i.getMemberId(),
                i.getIssueDate(), i.getStatus()
            });
        }
    }

    /** Loads active issues into the Issue tab table (for reference). */
    private void loadActiveIssuesIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Issue> issues = issueDAO.getActiveIssues();
        for (Issue i : issues) {
            model.addRow(new Object[]{
                i.getIssueId(), i.getBookId(), i.getMemberId(),
                i.getIssueDate(), i.getStatus()
            });
        }
    }

    /** Issues a book: validates selection, checks availability, calls DAO. */
    private void issueBook() {
        int memberIndex = memberCombo.getSelectedIndex();
        int bookIndex = bookCombo.getSelectedIndex();

        if (memberIndex == -1 || bookIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select both a member and a book.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Member member = membersList.get(memberIndex);
        Book book = booksList.get(bookIndex);

        if (book.getAvailable() <= 0) {
            JOptionPane.showMessageDialog(this,
                "Book is not available!",
                "Not Available", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Call IssueDAO.issueBook(bookId, memberId) — uses transaction internally
        if (issueDAO.issueBook(book.getBookId(), member.getMemberId())) {
            JOptionPane.showMessageDialog(this, "Book issued successfully!");
            loadComboBoxData(); // Refresh to update available counts
            loadActiveIssues();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to issue book. The book may no longer be available.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Returns a book: takes selected issue, calls DAO. */
    private void returnBook() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an issue record from the table to return.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int issueId = (int) issueTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm return of this book?", "Confirm Return", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (issueDAO.returnBook(issueId)) {
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
                loadActiveIssues();
                loadComboBoxData(); // Refresh to update available counts
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to return book.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
