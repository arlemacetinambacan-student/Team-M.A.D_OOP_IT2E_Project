package com.woms.gui.frames;

// RoleSelectionFrame.java - Choose login type
import java.awt.*;
import javax.swing.*;

public class RoleSelectionFrame extends JFrame {
    private GradientPanel backgroundPanel;
    
    public RoleSelectionFrame() {
        setTitle("WOMS - Select Login Type");
        setSize(750, 550);
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
        card.setPreferredSize(new Dimension(600, 420));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel title = new JLabel("<html><center>Work Opportunity Management System</center></html>");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 60, 120));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 15, 8, 15));
        headerPanel.add(title, BorderLayout.CENTER);
        
        card.add(headerPanel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Applicant Button (Main - big button)
        gbc.gridy = 0;
        JButton applicantBtn = createRoleButton("Apply / Applicant Portal", "", new Color(80, 180, 80));
        applicantBtn.addActionListener(e -> {
            dispose();
            new ApplicantSelectionFrame().setVisible(true);
        });
        buttonsPanel.add(applicantBtn, gbc);

        card.add(buttonsPanel, BorderLayout.CENTER);

        // Admin Login as clickable text (not button)
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        adminPanel.setOpaque(false);
        adminPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel adminLabel = new JLabel("Admin Login");
        adminLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        adminLabel.setForeground(new Color(60, 130, 255));
        adminLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new LoginFrame("ADMIN").setVisible(true);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                adminLabel.setForeground(new Color(40, 100, 200));
                adminLabel.setText("<html><u>Admin Login</u></html>");
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                adminLabel.setForeground(new Color(60, 130, 255));
                adminLabel.setText("Admin Login");
            }
        });
        adminPanel.add(adminLabel);
        
        card.add(adminPanel, BorderLayout.SOUTH);

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
            
            // Color palette that shifts smoothly (bluish tones)
            int r1 = (int) (240 + cycle * 10);
            int g1 = (int) (245 + cycle * 8);
            int b1 = (int) (255 + cycle * 5);
            
            int r2 = (int) (220 - cycle * 5);
            int green2 = (int) (230 - cycle * 8);
            int b2 = (int) (248 - cycle * 10);
            
            // Additional color variation
            float cycle2 = (float) (Math.sin((colorShift + 0.5) * Math.PI * 2) * 0.5 + 0.5);
            int r3 = (int) (235 + cycle2 * 15);
            int g3 = (int) (240 + cycle2 * 12);
            int b3 = (int) (250 + cycle2 * 8);
            
            // Blend colors for smooth gradient
            Color topColor = new Color(
                Math.min(255, Math.max(200, (r1 + r3) / 2)),
                Math.min(255, Math.max(200, (g1 + g3) / 2)),
                Math.min(255, Math.max(200, (b1 + b3) / 2))
            );
            Color bottomColor = new Color(
                Math.min(255, Math.max(200, r2)),
                Math.min(255, Math.max(200, green2)),
                Math.min(255, Math.max(200, b2))
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

