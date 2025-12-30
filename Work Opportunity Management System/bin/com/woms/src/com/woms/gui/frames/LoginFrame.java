package com.woms.gui.frames;

// LoginFrame.java (simple login experience)
import com.woms.database.Database;
import com.woms.gui.admin.MainGUI;
import com.woms.gui.applicant.ApplicantGUI;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passField;
    private JCheckBox showPassword;
    private JLabel statusLabel;
    private String loginRole;
    private char defaultEchoChar;
    private GradientPanel backgroundPanel;

    public LoginFrame(String role) {
        this.loginRole = role;
        setTitle("WOMS - " + (role.equals("ADMIN") ? "Admin" : "Applicant") + " Login");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        Color accent = new Color(74, 144, 226);  // Muted Blue #4A90E2
        Color muted = new Color(108, 117, 125);  // Steel Gray #6C757D
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        backgroundPanel = new GradientPanel();
        JPanel root = backgroundPanel;
        root.setLayout(new GridBagLayout());
        setContentPane(root);

        // ---- MAIN CARD ----
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(255, 255, 255, 240));
        card.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new Color(0,0,0,35), 1, true),
                BorderFactory.createEmptyBorder(35, 40, 35, 40)
        ));
        card.setPreferredSize(new Dimension(650, 500));

        // ---- HEADER ----
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Back button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setOpaque(false);
        JLabel backLabel = new JLabel("← Back");
        backLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backLabel.setForeground(new Color(74, 144, 226));  // Muted Blue #4A90E2
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                if (loginRole.equals("APPLICANT")) {
                    new ApplicantSelectionFrame().setVisible(true);
                } else {
                    new RoleSelectionFrame().setVisible(true);
                }
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backLabel.setForeground(new Color(27, 42, 65));  // Dark Blue #1B2A41
                backLabel.setText("<html><u>← Back</u></html>");
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backLabel.setForeground(new Color(74, 144, 226));  // Muted Blue #4A90E2
                backLabel.setText("← Back");
            }
        });
        backPanel.add(backLabel);
        header.add(backPanel, BorderLayout.WEST);

        JLabel title = new JLabel("<html><center>Work Opportunity Management System</center></html>");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(27, 42, 65));  // Dark Blue #1B2A41
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 15, 8, 15));

        JLabel subtitle = new JLabel(loginRole.equals("ADMIN") ? "Secure admin access" : "Applicant portal");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(muted);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);
        header.add(titlePanel, BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);

        // ---- FORM ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        // Username Label
        gbc.gridy = 0;
        JLabel uLabel = new JLabel("Username");
        uLabel.setFont(labelFont);
        form.add(uLabel, gbc);

        // Username Field
        gbc.gridy = 1;
        usernameField = new JTextField();
        usernameField.setFont(labelFont);
        usernameField.setPreferredSize(new Dimension(0, 35));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(usernameField);
        form.add(usernameField, gbc);

        // Password Label
        gbc.gridy = 2;
        JLabel pLabel = new JLabel("Password");
        pLabel.setFont(labelFont);
        form.add(pLabel, gbc);

        // Password Field
        gbc.gridy = 3;
        passField = new JPasswordField();
        defaultEchoChar = passField.getEchoChar();
        passField.setFont(labelFont);
        passField.setPreferredSize(new Dimension(0, 35));
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(passField);
        form.add(passField, gbc);

        // Show Password
        gbc.gridy = 4;
        showPassword = new JCheckBox("Show password");
        showPassword.setOpaque(false);
        showPassword.addActionListener(e -> togglePassword());
        form.add(showPassword, gbc);

        // Status Label
        gbc.gridy = 5;
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(180, 30, 30));
        form.add(statusLabel, gbc);

        // ---- SIGN IN BUTTON ----
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; // Allow expansion
        gbc.insets = new Insets(20, 0, 10, 0); // Full width spacing
        JButton loginBtn = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    super.paintComponent(g2);
                } finally {
                    g2.dispose();
                }
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground().darker());
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                } finally {
                    g2.dispose();
                }
            }
        };
        loginBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50)); // Full width, fixed height
        loginBtn.setMinimumSize(new Dimension(0, 50)); // Minimum width 0, fixed height
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Full width, fixed height
        loginBtn.setBackground(accent);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setContentAreaFilled(false);
        loginBtn.setOpaque(false);
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginBtn.setBackground(accent.brighter());
                loginBtn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginBtn.setBackground(accent);
                loginBtn.repaint();
            }
        });
        form.add(loginBtn, gbc);

        card.add(form, BorderLayout.CENTER);

        root.add(card);

        // Button Action
        loginBtn.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(loginBtn);
    }
    
    @Override
    public void dispose() {
        // Stop background animation
        if (backgroundPanel != null) {
            backgroundPanel.stopAnimation();
        }
        super.dispose();
    }

    private void togglePassword() {
        passField.setEchoChar(showPassword.isSelected() ? 0 : defaultEchoChar);
    }

    private void styleField(javax.swing.text.JTextComponent field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String pass = new String(passField.getPassword());

        if (username.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            boolean ok = false;
            if (loginRole.equals("ADMIN")) {
                ok = Database.adminAuthenticate(username, pass);
                if (ok) {
                    statusLabel.setText("Signing in...");
                    SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
                    dispose();
                } else {
                    statusLabel.setText("Invalid admin credentials.");
                }
            } else {
                ok = Database.applicantAuthenticate(username, pass);
                if (ok) {
                    statusLabel.setText("Signing in...");
                    SwingUtilities.invokeLater(() -> new ApplicantGUI(username).setVisible(true));
                    dispose();
                } else {
                    statusLabel.setText("Invalid applicant credentials.");
                }
            }
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private static class GradientPanel extends JPanel {
        private float colorShift = 0.0f;
        private Timer animationTimer;
        
        public GradientPanel() {
            // Start animation timer - updates every 50ms for smooth transition
            animationTimer = new Timer(50, e -> {
                colorShift += 0.01f;
                if (colorShift > 1.0f) {
                    colorShift = 0.0f;
                }
                repaint();
            });
            animationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // Create smooth color transitions using sine waves for natural movement
            float cycle = (float) (Math.sin(colorShift * Math.PI * 2) * 0.5 + 0.5);
            
            // Enterprise/Government color palette - Off-White #FAFAFA base with subtle variations
            // Base: Off-White #FAFAFA (250, 250, 250)
            int baseR = 250;
            int baseG = 250;
            int baseB = 250;
            
            // Subtle variation - very conservative (max 5 point variation)
            int r1 = (int) (baseR - cycle * 3);
            int g1 = (int) (baseG - cycle * 3);
            int b1 = (int) (baseB - cycle * 2);
            
            int r2 = (int) (baseR - cycle * 2);
            int green2 = (int) (baseG - cycle * 2);
            int b2 = (int) (baseB - cycle * 1);
            
            // Additional subtle variation
            float cycle2 = (float) (Math.sin((colorShift + 0.5) * Math.PI * 2) * 0.5 + 0.5);
            int r3 = (int) (baseR - cycle2 * 4);
            int g3 = (int) (baseG - cycle2 * 4);
            int b3 = (int) (baseB - cycle2 * 3);
            
            // Blend colors for smooth, professional gradient
            Color topColor = new Color(
                Math.min(255, Math.max(245, (r1 + r3) / 2)),
                Math.min(255, Math.max(245, (g1 + g3) / 2)),
                Math.min(255, Math.max(245, (b1 + b3) / 2))
            );
            Color bottomColor = new Color(
                Math.min(255, Math.max(245, r2)),
                Math.min(255, Math.max(245, green2)),
                Math.min(255, Math.max(245, b2))
            );
            
            GradientPaint gp = new GradientPaint(
                    0, 0, topColor,
                    0, getHeight(), bottomColor
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        
        public void stopAnimation() {
            if (animationTimer != null) {
                animationTimer.stop();
            }
        }
    }
}
