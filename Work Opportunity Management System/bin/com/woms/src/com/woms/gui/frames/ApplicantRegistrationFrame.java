package com.woms.gui.frames;

// ApplicantRegistrationFrame.java - Registration form for applicants
import com.woms.database.Database;
import com.woms.gui.applicant.ApplicantGUI;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

public class ApplicantRegistrationFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passField;
    private JPasswordField confirmPassField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField phoneField;
    private JCheckBox showPassword;
    private JLabel statusLabel;
    private char defaultEchoChar;

    public ApplicantRegistrationFrame() {
        setTitle("WOMS - Create Applicant Account");
        setSize(700, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color accent = new Color(80, 180, 80);
        Color muted = new Color(90, 90, 90);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 22);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);

        JPanel root = new GradientPanel();
        root.setLayout(new GridBagLayout());
        setContentPane(root);

        // ---- MAIN CARD ----
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(255, 255, 255, 240));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0,0,0,35), 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        card.setPreferredSize(new Dimension(600, 550));

        // ---- HEADER ----
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Create New Account");
        title.setFont(headerFont);
        title.setForeground(new Color(30, 60, 120));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Fill in your information to register");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(muted);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        card.add(header, BorderLayout.NORTH);

        // ---- FORM ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridy = 0;
        JLabel uLabel = new JLabel("Username *");
        uLabel.setFont(labelFont);
        uLabel.setForeground(new Color(60, 60, 60));
        form.add(uLabel, gbc);
        gbc.gridy = 1;
        usernameField = new JTextField();
        usernameField.setFont(labelFont);
        usernameField.setPreferredSize(new Dimension(0, 35));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(usernameField);
        form.add(usernameField, gbc);

        // Password
        gbc.gridy = 2;
        JLabel pLabel = new JLabel("Password *");
        pLabel.setFont(labelFont);
        pLabel.setForeground(new Color(60, 60, 60));
        form.add(pLabel, gbc);
        gbc.gridy = 3;
        passField = new JPasswordField();
        defaultEchoChar = passField.getEchoChar();
        passField.setFont(labelFont);
        passField.setPreferredSize(new Dimension(0, 35));
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(passField);
        form.add(passField, gbc);

        // Confirm Password
        gbc.gridy = 4;
        JLabel cpLabel = new JLabel("Confirm Password *");
        cpLabel.setFont(labelFont);
        cpLabel.setForeground(new Color(60, 60, 60));
        form.add(cpLabel, gbc);
        gbc.gridy = 5;
        confirmPassField = new JPasswordField();
        confirmPassField.setFont(labelFont);
        confirmPassField.setPreferredSize(new Dimension(0, 35));
        confirmPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(confirmPassField);
        form.add(confirmPassField, gbc);

        // Show Password
        gbc.gridy = 6;
        showPassword = new JCheckBox("Show password");
        showPassword.setOpaque(false);
        showPassword.addActionListener(e -> togglePassword());
        form.add(showPassword, gbc);

        // Name
        gbc.gridy = 7;
        JLabel nLabel = new JLabel("Full Name *");
        nLabel.setFont(labelFont);
        nLabel.setForeground(new Color(60, 60, 60));
        form.add(nLabel, gbc);
        gbc.gridy = 8;
        nameField = new JTextField();
        nameField.setFont(labelFont);
        nameField.setPreferredSize(new Dimension(0, 35));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(nameField);
        form.add(nameField, gbc);

        // Age
        gbc.gridy = 9;
        JLabel aLabel = new JLabel("Age *");
        aLabel.setFont(labelFont);
        aLabel.setForeground(new Color(60, 60, 60));
        form.add(aLabel, gbc);
        gbc.gridy = 10;
        ageField = new JTextField();
        ageField.setFont(labelFont);
        ageField.setPreferredSize(new Dimension(0, 35));
        ageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(ageField);
        form.add(ageField, gbc);

        // Phone
        gbc.gridy = 11;
        JLabel phLabel = new JLabel("Phone Number *");
        phLabel.setFont(labelFont);
        phLabel.setForeground(new Color(60, 60, 60));
        form.add(phLabel, gbc);
        gbc.gridy = 12;
        phoneField = new JTextField();
        phoneField.setFont(labelFont);
        phoneField.setPreferredSize(new Dimension(0, 35));
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(phoneField);
        form.add(phoneField, gbc);

        // Status Label
        gbc.gridy = 13;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(180, 30, 30));
        form.add(statusLabel, gbc);

        // ---- REGISTER BUTTON ----
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerBtn = new JButton("Create Account");
        registerBtn.setPreferredSize(new Dimension(250, 40));
        styleButton(registerBtn, accent);
        form.add(registerBtn, gbc);

        // Wrap form in scroll pane for better visibility
        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        card.add(scrollPane, BorderLayout.CENTER);

        // Back link
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setOpaque(false);
        JLabel backLabel = new JLabel("← Back to Login");
        backLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backLabel.setForeground(new Color(60, 130, 255));
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new ApplicantSelectionFrame().setVisible(true);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backLabel.setForeground(new Color(40, 100, 200));
                backLabel.setText("<html><u>← Back to Login</u></html>");
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backLabel.setForeground(new Color(60, 130, 255));
                backLabel.setText("← Back to Login");
            }
        });
        backPanel.add(backLabel);
        card.add(backPanel, BorderLayout.SOUTH);

        root.add(card);

        // Button Action
        registerBtn.addActionListener(e -> doRegister());
        getRootPane().setDefaultButton(registerBtn);
    }

    private void togglePassword() {
        char echo = showPassword.isSelected() ? 0 : defaultEchoChar;
        passField.setEchoChar(echo);
        confirmPassField.setEchoChar(echo);
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String pass = new String(passField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validation
        if (username.isEmpty() || pass.isEmpty() || name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
            statusLabel.setText("Please fill in all required fields.");
            return;
        }

        if (!pass.equals(confirmPass)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        // Username validation: 3–25 chars, no spaces
        if (!username.matches("^[^\\s]{3,25}$")) {
            statusLabel.setText("Username must be 3–25 characters with no spaces.");
            return;
        }

        // Password validation:
        // - at least 8 characters
        // - at least 1 uppercase, 1 lowercase, 1 digit, 1 special character
        if (!pass.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")) {
            statusLabel.setText("Password must be 8+ chars with uppercase, lowercase, number, and special character.");
            return;
        }

        // Age validation: only 18–60 allowed
        if (!ageStr.matches("^(1[89]|[2-5]\\d|60)$")) {
            statusLabel.setText("Age must be between 18 and 60.");
            return;
        }
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            statusLabel.setText("Age must be a valid number.");
            return;
        }

        // Phone validation: 09 + 9 digits = exactly 11 digits, numbers only
        if (!phone.matches("^09\\d{9}$")) {
            statusLabel.setText("Phone number must be 11 digits and start with 09.");
            return;
        }

        try {
            // Check if username already exists
            try (java.sql.Connection conn = Database.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?")) {
                ps.setString(1, username);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    statusLabel.setText("Username already taken. Please choose another.");
                    return;
                }
            }

            // Create applicant account
            Database.createApplicant(username, pass, name, age, phone);
            statusLabel.setText("Account created successfully! Redirecting...");
            statusLabel.setForeground(new Color(60, 180, 80));
            
            // Auto-login and redirect to applicant portal
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(1000);
                    dispose();
                    new ApplicantGUI(username).setVisible(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
            statusLabel.setForeground(new Color(180, 30, 30));
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(new Color(180, 30, 30));
        }
    }

    private void styleField(JTextComponent field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void styleButton(JButton b, Color accent) {
        b.setBackground(accent);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(accent.darker(), 1, true));
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(240, 245, 255),
                    0, getHeight(), new Color(220, 230, 248)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

