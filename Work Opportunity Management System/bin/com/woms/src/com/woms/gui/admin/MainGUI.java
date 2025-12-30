//manages job, opportunities, application, training, and employment tracking to 
//poverty reduction

package com.woms.gui.admin;

import com.woms.gui.frames.RoleSelectionFrame;
import com.woms.gui.panels.ApplicantsPanel;
import com.woms.gui.panels.ApplicationsPanel;
import com.woms.gui.panels.EmploymentStatusPanel;
import com.woms.gui.panels.InterviewsPanel;
import com.woms.gui.panels.JobsPanel;
import com.woms.gui.panels.ReportsPanel;
import com.woms.gui.panels.SkillsTrainingPanel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class MainGUI extends JFrame {
    private final JTabbedPane tabs;
    private final JLabel clockLabel;
    private final JLabel userLabel;
    private final DateTimeFormatter clockFmt = DateTimeFormatter.ofPattern("EEE, MMM d  yyyy  |  hh:mm:ss a");
    private Timer clockTimer;
    private GradientBackgroundPanel backgroundPanel;

    public MainGUI() {
        setTitle("Work Opportunity Management System - Admin Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));

        backgroundPanel = new GradientBackgroundPanel();
        JPanel root = backgroundPanel;
        root.setLayout(new BorderLayout());
        setContentPane(root);

        // Enhanced Header with gradient
        JPanel header = new GradientHeaderPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(108, 117, 125)),  // Steel Gray #6C757D
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Left side - Logo and Title
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);
        leftHeader.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        JLabel lbl = new JLabel("Work Opportunity Management System");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(new Color(255, 255, 255));
        leftHeader.add(lbl);
        
        header.add(leftHeader, BorderLayout.WEST);

        // Right side - User info, Logout button, and Clock
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);
        rightHeader.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        userLabel = new JLabel("teammad");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(new Color(255, 255, 255));
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        rightHeader.add(userLabel);
        
        // Logout Button with rounded corners
        JButton logoutBtn = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                } finally {
                    g2.dispose();
                }
            }
        };
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(255, 255, 255));
        logoutBtn.setForeground(new Color(108, 117, 125));  // Steel Gray #6C757D
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setOpaque(false);
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                logoutBtn.setBackground(new Color(250, 250, 250));  // Off-White #FAFAFA
                logoutBtn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                logoutBtn.setBackground(Color.WHITE);
                logoutBtn.repaint();
            }
        });
        logoutBtn.addActionListener(e -> logout());
        rightHeader.add(logoutBtn);
        
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clockLabel.setForeground(new Color(255, 255, 255, 220));
        clockLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        rightHeader.add(clockLabel);
        
        header.add(rightHeader, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // Enhanced Tabbed Pane with enterprise styling
        tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(new Color(250, 250, 250));  // Off-White #FAFAFA
        tabs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Customize tab appearance - Enterprise style
        UIManager.put("TabbedPane.selected", new Color(27, 42, 65));  // Dark Blue #1B2A41
        UIManager.put("TabbedPane.background", new Color(250, 250, 250));  // Off-White #FAFAFA
        UIManager.put("TabbedPane.contentAreaColor", new Color(255, 255, 255));
        UIManager.put("TabbedPane.tabAreaBackground", new Color(250, 250, 250));
        
        // Add tabs
        tabs.addTab("Applicants", new ApplicantsPanel());
        tabs.addTab("Jobs", new JobsPanel());
        tabs.addTab("Applications", new ApplicationsPanel());
        tabs.addTab("Interviews", new InterviewsPanel());
        tabs.addTab("Skills & Training", new SkillsTrainingPanel());
        tabs.addTab("Employment Status", new EmploymentStatusPanel());
        tabs.addTab("Reports/Export", new ReportsPanel());

        // Style tabs with enterprise colors
        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.setBackgroundAt(i, new Color(250, 250, 250));  // Off-White #FAFAFA
        }

        root.add(tabs, BorderLayout.CENTER);

        // Enterprise Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(250, 250, 250));  // Off-White #FAFAFA
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(108, 117, 125)),  // Steel Gray #6C757D
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(27, 42, 65));  // Dark Blue #1B2A41
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        JLabel versionLabel = new JLabel("WOMS v1.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        versionLabel.setForeground(new Color(108, 117, 125));  // Steel Gray #6C757D
        statusBar.add(versionLabel, BorderLayout.EAST);
        
        root.add(statusBar, BorderLayout.SOUTH);

        // Start live clock
        updateClock();
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
    }

    private void updateClock() {
        clockLabel.setText(clockFmt.format(LocalDateTime.now()));
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Stop the clock timer before disposing
            if (clockTimer != null) {
                clockTimer.stop();
            }
            dispose(); // Close MainGUI
            // Show role selection frame again
            SwingUtilities.invokeLater(() -> {
                RoleSelectionFrame roleFrame = new RoleSelectionFrame();
                roleFrame.setVisible(true);
            });
        }
    }
    
    @Override
    public void dispose() {
        // Clean up timers when frame is disposed
        if (clockTimer != null) {
            clockTimer.stop();
            clockTimer = null;
        }
        // Stop background animation
        if (backgroundPanel != null) {
            backgroundPanel.stopAnimation();
        }
        super.dispose();
    }
    
    // Animated color-shifting gradient background panel
    private static class GradientBackgroundPanel extends JPanel {
        private float colorShift = 0.0f;
        private Timer animationTimer;
        
        public GradientBackgroundPanel() {
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
    
    // Enterprise/Government-style gradient header panel
    private static class GradientHeaderPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            // Dark Blue #1B2A41 to slightly darker
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(27, 42, 65),  // Primary Dark Blue
                    0, getHeight(), new Color(20, 32, 50)  // Darker shade
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
