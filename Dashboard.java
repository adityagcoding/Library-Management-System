package ui;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {
        setTitle("Library Management System - Dashboard");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        Color primaryColor = new Color(44, 62, 80);
        Color backgroundColor = new Color(236, 240, 241);
        Color accentColor = new Color(52, 152, 219);
        Color dangerColor = new Color(231, 76, 60);
        Color white = Color.WHITE;

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel welcomeLabel = new JLabel("Welcome to Library Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(primaryColor);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Grid for buttons
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        gridPanel.setBackground(backgroundColor);

        JButton btnManageBooks = createMenuButton("📚 Manage Books", accentColor, white);
        JButton btnManageMembers = createMenuButton("👥 Manage Members", accentColor, white);
        JButton btnIssueBook = createMenuButton("📖 Issue Book", accentColor, white);
        JButton btnReturnBook = createMenuButton("🔄 Return Book", accentColor, white);
        JButton btnSearchBooks = createMenuButton("🔍 Search Books", accentColor, white);
        JButton btnLogout = createMenuButton("🚪 Logout", dangerColor, white);

        gridPanel.add(btnManageBooks);
        gridPanel.add(btnManageMembers);
        gridPanel.add(btnIssueBook);
        gridPanel.add(btnReturnBook);
        gridPanel.add(btnSearchBooks);
        gridPanel.add(btnLogout);

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Action Listeners
        btnManageBooks.addActionListener(e -> new BookManagement().setVisible(true));
        btnManageMembers.addActionListener(e -> new MemberManagement().setVisible(true));
        
        btnIssueBook.addActionListener(e -> {
            IssueReturn ir = new IssueReturn();
            ir.setTab(0);
            ir.setVisible(true);
        });
        
        btnReturnBook.addActionListener(e -> {
            IssueReturn ir = new IssueReturn();
            ir.setTab(1);
            ir.setVisible(true);
        });

        btnSearchBooks.addActionListener(e -> new BookManagement().setVisible(true));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                this.dispose();
            }
        });
    }

    private JButton createMenuButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 80));
        return button;
    }
}
