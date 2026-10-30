package ui;

import dao.MemberDAO;
import model.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Member Management window.
 * Allows admin to Add, Update, Delete, Search, and View all members.
 */
public class MemberManagement extends JFrame {
    private JTextField txtName, txtContact, txtEmail, txtSearch;
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private MemberDAO memberDAO;
    private int selectedMemberId = -1;

    public MemberManagement() {
        memberDAO = new MemberDAO();

        setTitle("Member Management");
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

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createTitledBorder("Member Details"));

        formPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Contact:"));
        txtContact = new JTextField();
        formPanel.add(txtContact);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        // Spacers for alignment
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

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
        String[] columns = {"ID", "Name", "Contact", "Email", "Membership Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        memberTable.setFont(new Font("Arial", Font.PLAIN, 14));
        memberTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(memberTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // === Event Listeners ===

        // When a table row is clicked, populate the form fields
        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && memberTable.getSelectedRow() != -1) {
                int row = memberTable.getSelectedRow();
                selectedMemberId = (int) tableModel.getValueAt(row, 0);
                txtName.setText(safeString(tableModel.getValueAt(row, 1)));
                txtContact.setText(safeString(tableModel.getValueAt(row, 2)));
                txtEmail.setText(safeString(tableModel.getValueAt(row, 3)));
            }
        });

        btnAdd.addActionListener(e -> addMember());
        btnUpdate.addActionListener(e -> updateMember());
        btnDelete.addActionListener(e -> deleteMember());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchMembers());

        // Load all members when window opens
        refreshTable();
    }

    /** Safe conversion of table value to string */
    private String safeString(Object obj) {
        return obj != null ? obj.toString() : "";
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
        txtName.setText("");
        txtContact.setText("");
        txtEmail.setText("");
        txtSearch.setText("");
        selectedMemberId = -1;
        memberTable.clearSelection();
    }

    private void refreshTable() {
        loadTableData(memberDAO.getAllMembers());
    }

    private void searchMembers() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
        } else {
            loadTableData(memberDAO.searchMembers(keyword));
        }
    }

    private void loadTableData(List<Member> members) {
        tableModel.setRowCount(0);
        for (Member m : members) {
            tableModel.addRow(new Object[]{
                m.getMemberId(), m.getName(), m.getContact(),
                m.getEmail(), m.getMembershipDate()
            });
        }
    }

    private void addMember() {
        String name = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        String email = txtEmail.getText().trim();

        // Validation
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Member member = new Member(0, name, contact, email, "");
        if (memberDAO.addMember(member)) {
            JOptionPane.showMessageDialog(this, "Member added successfully!");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add member.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        String email = txtEmail.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Member member = new Member(selectedMemberId, name, contact, email, "");
        if (memberDAO.updateMember(member)) {
            JOptionPane.showMessageDialog(this, "Member updated successfully!");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update member.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this member?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (memberDAO.deleteMember(selectedMemberId)) {
                JOptionPane.showMessageDialog(this, "Member deleted successfully!");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Cannot delete this member. They may have active book issues.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
