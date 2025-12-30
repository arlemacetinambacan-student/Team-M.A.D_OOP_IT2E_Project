package com.woms.gui.frames;

// ApplicantSelectionFrame.java - Choose between Create Account or Login
import java.awt.*;
import javax.swing.*;

public class ApplicantSelectionFrame extends JFrame {
    private GradientPanel backgroundPanel;
    
    public ApplicantSelectionFrame() {
        setTitle("WOMS - Applicant Portal");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        backgroundPanel = new GradientPanel();
        JPanel root = backgroundPanel;
        root.setLayout(new GridBagLayout());
        setContentPane(root);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(255, 255, 255, 240));
        card.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new Color(0,0,0,35), 1, true),
                BorderFactory.createEmptyBorder(50, 50, 50, 50)
        ));
        card.setPreferredSize(new Dimension(600, 480));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel title = new JLabel("<html><center>Work Opportunity Management System</center></html>");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(27, 42, 65));  // Dark Blue #1B2A41
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 15, 8, 15));
        headerPanel.add(title, BorderLayout.CENTER);
        
        card.add(headerPanel, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Applicant Portal");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subtitle.setForeground(new Color(108, 117, 125));  // Steel Gray #6C757D
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
        card.add(subtitle, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Create Account Button (Main - big button)
        gbc.gridy = 0;
        JButton createAccountBtn = createRoleButton("Create New Account", "", new Color(80, 180, 80));
        createAccountBtn.addActionListener(e -> {
            dispose();
            new ApplicantRegistrationFrame().setVisible(true);
        });
        buttonsPanel.add(createAccountBtn, gbc);

        card.add(buttonsPanel, BorderLayout.CENTER);

        // Login as clickable text (not button)
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel loginLabel = new JLabel("Already have an account? Login");
        loginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        loginLabel.setForeground(new Color(60, 130, 255));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new LoginFrame("APPLICANT").setVisible(true);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginLabel.setForeground(new Color(27, 42, 65));  // Dark Blue #1B2A41
                loginLabel.setText("<html><u>Already have an account? Login</u></html>");
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginLabel.setForeground(new Color(74, 144, 226));  // Muted Blue #4A90E2
                loginLabel.setText("Already have an account? Login");
            }
        });
        loginPanel.add(loginLabel);
        
        card.add(loginPanel, BorderLayout.SOUTH);

        // Back button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setOpaque(false);
        backPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel backLabel = new JLabel("← Back");
        backLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backLabel.setForeground(new Color(100, 100, 100));
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new RoleSelectionFrame().setVisible(true);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backLabel.setForeground(new Color(74, 144, 226));  // Muted Blue #4A90E2
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backLabel.setForeground(new Color(108, 117, 125));  // Steel Gray #6C757D
            }
        });
        backPanel.add(backLabel);
        card.add(backPanel, BorderLayout.NORTH);

        root.add(card);
    }
    
    @Override
    public void dispose() {
        // Stop background animation
        if (backgroundPanel != null) {
            backgroundPanel.stopAnimation();
        }
        super.dispose();
    }

    private JButton createRoleButton(String text, String icon, Color color) {
        String buttonText = icon.isEmpty() ? text : "<html><center>" + icon + "<br>" + text + "</center></html>";
        JButton btn = new JButton(buttonText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
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
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                } finally {
                    g2.dispose();
                }
            }
        };
        btn.setPreferredSize(new Dimension(320, 90));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(color.brighter());
                btn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(color);
                btn.repaint();
            }
        });
        return btn;
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

