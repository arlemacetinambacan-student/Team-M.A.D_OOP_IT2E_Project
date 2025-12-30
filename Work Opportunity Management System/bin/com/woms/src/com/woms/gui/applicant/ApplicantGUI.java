package com.woms.gui.applicant;

// ApplicantGUI.java - GUI for applicants
import com.woms.database.Database;
import com.woms.gui.frames.JobApplicationForm;
import com.woms.gui.frames.RoleSelectionFrame;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ApplicantGUI extends JFrame {
    private String applicantUsername;
    private int applicantId;
    private JTabbedPane tabs;
    private JLabel dateTimeLabel;
    private Timer clockTimer;
    private GradientPanel backgroundPanel;

    public ApplicantGUI(String username) {
        this.applicantUsername = username;
        try {
            this.applicantId = Database.getApplicantUserId(username);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error getting applicant ID: " + e.getMessage());
            dispose();
            return;
        }

        setTitle("WOMS - Applicant Portal");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));

        backgroundPanel = new GradientPanel();
        JPanel root = backgroundPanel;
        root.setLayout(new BorderLayout());
        setContentPane(root);

        // Enterprise Header with gradient
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(27, 42, 65));  // Dark Blue #1B2A41
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);
        
        JLabel systemLabel = new JLabel("Work Opportunity Management System");
        systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        systemLabel.setForeground(new Color(255, 255, 255, 240));
        leftHeader.add(systemLabel);
        
        JLabel separator = new JLabel("|");
        separator.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        separator.setForeground(new Color(255, 255, 255, 180));
        separator.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        leftHeader.add(separator);
        
        JLabel lbl = new JLabel("Applicant Portal");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);
        leftHeader.add(lbl);
        
        header.add(leftHeader, BorderLayout.WEST);

        // Right side - Date/Time, User and Logout
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);
        
        // Date and Time Display
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateTimeLabel.setForeground(new Color(255, 255, 255, 220));
        dateTimeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        updateDateTime();
        rightHeader.add(dateTimeLabel);
        
        // Start clock timer to update every second
        clockTimer = new Timer(1000, e -> updateDateTime());
        clockTimer.start();
        
        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        rightHeader.add(userLabel);
        
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
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.0f));
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
                logoutBtn.setBackground(new Color(245, 245, 245));
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
        
        header.add(rightHeader, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Enterprise Tabs with professional styling
        tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(new Color(250, 250, 250));  // Off-White #FAFAFA
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Customize tab appearance - Enterprise style
        UIManager.put("TabbedPane.selected", new Color(27, 42, 65));  // Dark Blue #1B2A41
        UIManager.put("TabbedPane.background", new Color(250, 250, 250));  // Off-White #FAFAFA
        UIManager.put("TabbedPane.contentAreaColor", new Color(255, 255, 255));
        
        tabs.addTab("Available Jobs", new ApplicantJobsPanel(applicantId));
        tabs.addTab("Recommended Jobs", new ApplicantRecommendedJobsPanel(applicantId));
        tabs.addTab("My Applications", new ApplicantApplicationsPanel(applicantId));
        tabs.addTab("Attendance", new ApplicantAttendancePanel(applicantId));
        tabs.addTab("Training Programs", new ApplicantTrainingPanel(applicantId));
        tabs.addTab("Submit My Needs", new ApplicantNeedsPanel(applicantId));
        
        root.add(tabs, BorderLayout.CENTER);
    }

    private void updateDateTime() {
        if (dateTimeLabel != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
            Date now = new Date();
            String dateStr = dateFormat.format(now);
            String timeStr = timeFormat.format(now);
            dateTimeLabel.setText(dateStr + " | " + timeStr);
        }
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
            dispose();
            SwingUtilities.invokeLater(() -> {
                RoleSelectionFrame roleFrame = new RoleSelectionFrame();
                roleFrame.setVisible(true);
            });
        }
    }
    
    @Override
    public void dispose() {
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

    // Panel for viewing available jobs
    private class ApplicantJobsPanel extends JPanel {
        private DefaultTableModel model;
        private JTable table;
        private int applicantId;

        public ApplicantJobsPanel(int applicantId) {
            this.applicantId = applicantId;
            setLayout(new BorderLayout());
            setBackground(new Color(248, 250, 252));

            // Modern title panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(new Color(255, 255, 255));
            titlePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(20, 25, 20, 25)
            ));
            
            JLabel title = new JLabel("Available Job Opportunities");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(new Color(40, 80, 200));
            titlePanel.add(title, BorderLayout.WEST);
            
            JLabel subtitle = new JLabel("Select a job and click Apply to submit your application");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(120, 120, 120));
            titlePanel.add(subtitle, BorderLayout.SOUTH);
            
            add(titlePanel, BorderLayout.NORTH);

            // Modern table with better styling
            model = new DefaultTableModel(new String[]{"Job ID","Job Title","Description","Salary (₱)","Status"},0) {
                @Override public boolean isCellEditable(int r,int c){ 
                    return false;
                }
            };
            table = new JTable(model);
            table.setRowHeight(35);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setBackground(new Color(60, 130, 255));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setPreferredSize(new Dimension(0, 40));
            table.setSelectionBackground(new Color(220, 235, 255));
            table.setSelectionForeground(new Color(40, 80, 200));
            table.setGridColor(new Color(230, 235, 240));
            table.setShowGrid(true);
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            scrollPane.setBackground(Color.WHITE);
            add(scrollPane, BorderLayout.CENTER);

            // Modern button panel
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            btnPanel.setBackground(new Color(255, 255, 255));
            btnPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            
            JButton applyBtn = ApplicantGUI.this.createModernButton("Apply for Selected Job", new Color(74, 144, 226));  // Muted Blue #4A90E2
            applyBtn.setPreferredSize(new Dimension(250, 45));
            applyBtn.addActionListener(e -> {
                int r = table.getSelectedRow();
                if (r == -1) {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a job from the table to apply.",
                        "No Job Selected",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int jobId = (int) model.getValueAt(r, 0);
                String jobTitle = (String) model.getValueAt(r, 1);
                
                // Show application form with job information
                JobApplicationForm form = new JobApplicationForm(ApplicantGUI.this, jobId, jobTitle);
                form.setVisible(true);
                
                if (form.isSubmitted()) {
                    JobApplicationForm.ApplicationData data = form.getData();
                    try {
                        Database.createApplication(applicantId, data.jobId, data.firstName, data.middleName, 
                                data.lastName, data.gender, data.age, data.address, data.experience);
                        JOptionPane.showMessageDialog(this, 
                            "Application submitted successfully!\n\nYou can check the status in 'My Applications' tab.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        refresh();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Error submitting application:\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            btnPanel.add(applyBtn);
            
            JButton refreshBtn = ApplicantGUI.this.createModernButton("Refresh", new Color(100, 120, 150));
            refreshBtn.setPreferredSize(new Dimension(150, 45));
            refreshBtn.addActionListener(e -> refresh());
            btnPanel.add(refreshBtn);
            
            add(btnPanel, BorderLayout.SOUTH);

            refresh();
        }

        private void refresh() {
            try {
                model.setRowCount(0);
                List<Database.JobRecord> jobs = Database.getAllJobs();
                for (Database.JobRecord j : jobs) {
                    String desc = j.description != null ? j.description : "";
                    String shortDesc = desc.length() > 60 ? 
                        desc.substring(0, 60) + "..." : desc;
                    model.addRow(new Object[]{j.id, j.title != null ? j.title : "N/A", shortDesc, 
                        String.format("₱%,.2f", j.salary), "Available"});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading jobs: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

    }
    
    // Shared method for creating modern buttons with rounded corners
    private JButton createModernButton(String text, Color color) {
        JButton b = new JButton(text) {
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
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(color.brighter());
                b.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(color);
                b.repaint();
            }
        });
        return b;
    }

    // Panel for viewing applicant's applications
    private class ApplicantApplicationsPanel extends JPanel {
        private DefaultTableModel model;
        private JTable table;
        private int applicantId;

        public ApplicantApplicationsPanel(int applicantId) {
            this.applicantId = applicantId;
            setLayout(new BorderLayout());
            setBackground(new Color(248, 250, 252));

            // Modern title panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(new Color(255, 255, 255));
            titlePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(20, 25, 20, 25)
            ));
            
            JLabel title = new JLabel("My Job Applications");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(new Color(40, 80, 200));
            titlePanel.add(title, BorderLayout.WEST);
            
            JLabel subtitle = new JLabel("Track the status of your submitted applications");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(120, 120, 120));
            titlePanel.add(subtitle, BorderLayout.SOUTH);
            
            add(titlePanel, BorderLayout.NORTH);

            // Modern table with status colors
            model = new DefaultTableModel(new String[]{"Application ID","Job Title","Status","Date Submitted"},0) {
                @Override public boolean isCellEditable(int r,int c){ return false; }
            };
            table = new JTable(model);
            table.setRowHeight(35);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setBackground(new Color(60, 130, 255));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setPreferredSize(new Dimension(0, 40));
            table.setSelectionBackground(new Color(220, 235, 255));
            table.setSelectionForeground(new Color(40, 80, 200));
            table.setGridColor(new Color(230, 235, 240));
            table.setShowGrid(true);
            
            // Color code status column
            table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (column == 2 && value != null) {
                        String status = value.toString().toUpperCase();
                        if ("APPROVED".equals(status)) {
                            c.setForeground(new Color(60, 180, 80));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if ("REJECTED".equals(status)) {
                            c.setForeground(new Color(220, 60, 60));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(new Color(255, 165, 0));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    }
                    return c;
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            scrollPane.setBackground(Color.WHITE);
            add(scrollPane, BorderLayout.CENTER);
            
            // Refresh button panel
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            btnPanel.setBackground(new Color(255, 255, 255));
            btnPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            
            JButton refreshBtn = ApplicantGUI.this.createModernButton("Refresh Applications", new Color(100, 120, 150));
            refreshBtn.setPreferredSize(new Dimension(200, 40));
            refreshBtn.addActionListener(e -> refresh());
            btnPanel.add(refreshBtn);
            
            add(btnPanel, BorderLayout.SOUTH);

            refresh();
        }

        private void refresh() {
            try {
                model.setRowCount(0);
                List<Database.ApplicationRecord> apps = Database.getAllApplications();
                int count = 0;
                for (Database.ApplicationRecord a : apps) {
                    if (a.applicantId == applicantId) {
                        // Format submission date
                        String dateStr = "N/A";
                        if (a.submissionDate != null && !a.submissionDate.isEmpty()) {
                            try {
                                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(a.submissionDate);
                                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
                                dateStr = dateTime.format(formatter);
                            } catch (Exception e) {
                                dateStr = a.submissionDate; // Use raw string if parsing fails
                            }
                        }
                        model.addRow(new Object[]{a.id, a.jobTitle, a.status, dateStr});
                        count++;
                    }
                }
                if (count == 0) {
                    model.addRow(new Object[]{"-", "No applications yet", "-", "-"});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading applications: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Panel for submitting applicant needs
    private class ApplicantNeedsPanel extends JPanel {
        private DefaultTableModel model;
        private JTable table;
        private JTextArea needDescription;
        private JComboBox<String> categoryCombo;
        private JButton submitBtn;
        private int applicantId;

        public ApplicantNeedsPanel(int applicantId) {
            this.applicantId = applicantId;
            setLayout(new BorderLayout());
            setBackground(new Color(248, 250, 252));

            // Modern title panel
            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(new Color(255, 255, 255));
            titlePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(20, 25, 20, 25)
            ));
            
            JLabel title = new JLabel("Submit My Needs / Requirements");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(new Color(40, 80, 200));
            titlePanel.add(title, BorderLayout.WEST);
            
            JLabel subtitle = new JLabel("Request assistance or resources you need");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(120, 120, 120));
            titlePanel.add(subtitle, BorderLayout.SOUTH);
            
            add(titlePanel, BorderLayout.NORTH);

            // Modern Form Panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(new Color(255, 255, 255));
            formPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 235, 240), 1, true),
                    BorderFactory.createEmptyBorder(25, 25, 25, 25)
            ));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(12, 12, 12, 12);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            // Category Label
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            JLabel catLabel = new JLabel("Category:");
            catLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            catLabel.setForeground(new Color(60, 60, 60));
            formPanel.add(catLabel, gbc);

            // Category Combo
            gbc.gridx = 1; gbc.gridy = 0;
            categoryCombo = new JComboBox<>(new String[]{
                "Job Application", "Financial Assistance", "Housing Needs", 
                "Food Assistance", "Medical Needs", "Education Support", "Other"
            });
            categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            categoryCombo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            formPanel.add(categoryCombo, gbc);

            // Description Label
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            JLabel descLabel = new JLabel("Description:");
            descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            descLabel.setForeground(new Color(60, 60, 60));
            formPanel.add(descLabel, gbc);

            // Description Text Area
            gbc.gridx = 0; gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;
            needDescription = new JTextArea(8, 40);
            needDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            needDescription.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            needDescription.setLineWrap(true);
            needDescription.setWrapStyleWord(true);
            needDescription.setBackground(new Color(252, 252, 252));
            formPanel.add(new JScrollPane(needDescription), gbc);

            // Submit Button
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 0;
            submitBtn = ApplicantGUI.this.createModernButton("Submit Need Request", new Color(74, 144, 226));  // Muted Blue #4A90E2
            submitBtn.setPreferredSize(new Dimension(0, 45));
            submitBtn.addActionListener(e -> {
                String desc = needDescription.getText().trim();
                String category = (String) categoryCombo.getSelectedItem();
                if (desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a description of your need.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                    needDescription.requestFocus();
                    return;
                }
                try {
                    Database.createApplicantNeed(applicantId, desc, category);
                    JOptionPane.showMessageDialog(this, 
                        "Your need has been submitted successfully!\n\nWe will review your request and get back to you soon.",
                        "Request Submitted",
                        JOptionPane.INFORMATION_MESSAGE);
                    needDescription.setText("");
                    categoryCombo.setSelectedIndex(0);
                    refresh();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error submitting request:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            formPanel.add(submitBtn, gbc);

            // Split pane for form and table
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setTopComponent(formPanel);
            splitPane.setBottomComponent(createNeedsTable());
            splitPane.setDividerLocation(300);
            splitPane.setResizeWeight(0.5);
            add(splitPane, BorderLayout.CENTER);
        }

        private JPanel createNeedsTable() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(255, 255, 255));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 235, 240), 1, true),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            JLabel tableTitle = new JLabel("My Submitted Needs");
            tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            tableTitle.setForeground(new Color(40, 80, 200));
            tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            panel.add(tableTitle, BorderLayout.NORTH);

            model = new DefaultTableModel(new String[]{"ID","Category","Description","Status","Date Submitted"},0) {
                @Override public boolean isCellEditable(int r,int c){ return false; }
            };
            table = new JTable(model);
            table.setRowHeight(35);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setBackground(new Color(60, 130, 255));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setPreferredSize(new Dimension(0, 40));
            table.setSelectionBackground(new Color(220, 235, 255));
            table.setSelectionForeground(new Color(40, 80, 200));
            table.setGridColor(new Color(230, 235, 240));
            table.setShowGrid(true);
            table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (column == 3 && value != null) {
                        String status = value.toString().toUpperCase();
                        if ("APPROVED".equals(status)) {
                            c.setForeground(new Color(60, 180, 80));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if ("REJECTED".equals(status)) {
                            c.setForeground(new Color(220, 60, 60));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(new Color(255, 165, 0));
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    }
                    return c;
                }
            });
            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            refresh();
            return panel;
        }

        private void refresh() {
            try {
                model.setRowCount(0);
                List<Database.ApplicantNeedRecord> needs = Database.getApplicantNeeds(applicantId);
                for (Database.ApplicantNeedRecord n : needs) {
                    String desc = n.description != null ? n.description : "";
                    String shortDesc = desc.length() > 60 ? 
                        desc.substring(0, 60) + "..." : desc;
                    String date = n.submittedDate != null && n.submittedDate.length() > 10 ? 
                        n.submittedDate.substring(0, 10) : (n.submittedDate != null ? n.submittedDate : "N/A");
                    model.addRow(new Object[]{n.id, n.category, shortDesc, n.status, date});
                }
                if (needs.isEmpty()) {
                    model.addRow(new Object[]{"-", "No needs submitted yet", "-", "-", "-"});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading needs: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Panel for recommended jobs based on skills
    private class ApplicantRecommendedJobsPanel extends JPanel {
        private DefaultTableModel model;
        private JTable table;
        private int applicantId;

        public ApplicantRecommendedJobsPanel(int applicantId) {
            this.applicantId = applicantId;
            setLayout(new BorderLayout());
            setBackground(new Color(248, 250, 252));

            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(new Color(255, 255, 255));
            titlePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(20, 25, 20, 25)
            ));
            
            JLabel title = new JLabel("Recommended Jobs (Based on Your Skills)");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(new Color(40, 80, 200));
            titlePanel.add(title, BorderLayout.WEST);
            
            JLabel subtitle = new JLabel("Jobs matched to your skills - Higher match percentage means better fit!");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(120, 120, 120));
            titlePanel.add(subtitle, BorderLayout.SOUTH);
            
            add(titlePanel, BorderLayout.NORTH);

            model = new DefaultTableModel(new String[]{"Job ID", "Job Title", "Salary (₱)", "Match %", "Action"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            table = new JTable(model);
            table.setRowHeight(35);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setBackground(new Color(60, 130, 255));
            table.getTableHeader().setForeground(Color.WHITE);
            table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (column == 3 && value != null) {
                        try {
                            int match = Integer.parseInt(value.toString().replace("%", ""));
                            if (match >= 80) {
                                c.setForeground(new Color(60, 180, 80));
                                c.setFont(c.getFont().deriveFont(Font.BOLD));
                            } else if (match >= 50) {
                                c.setForeground(new Color(255, 165, 0));
                                c.setFont(c.getFont().deriveFont(Font.BOLD));
                            } else {
                                c.setForeground(new Color(220, 60, 60));
                            }
                        } catch (Exception e) {
                            // Ignore parsing errors
                        }
                    }
                    return c;
                }
            });
            add(new JScrollPane(table), BorderLayout.CENTER);

            JButton applyBtn = ApplicantGUI.this.createModernButton("Apply for Selected Job", new Color(74, 144, 226));  // Muted Blue #4A90E2
            applyBtn.setPreferredSize(new Dimension(250, 45));
            applyBtn.addActionListener(e -> {
                int r = table.getSelectedRow();
                if (r == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a job to apply.");
                    return;
                }
                int jobId = (int) model.getValueAt(r, 0);
                String jobTitle = (String) model.getValueAt(r, 1);
                
                // Show application form with job information
                JobApplicationForm form = new JobApplicationForm(ApplicantGUI.this, jobId, jobTitle);
                form.setVisible(true);
                
                if (form.isSubmitted()) {
                    JobApplicationForm.ApplicationData data = form.getData();
                    try {
                        Database.createApplication(applicantId, data.jobId, data.firstName, data.middleName, 
                                data.lastName, data.gender, data.age, data.address, data.experience);
                        JOptionPane.showMessageDialog(this, "Application submitted successfully!");
                        refresh();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                    }
                }
            });

            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(new Color(255, 255, 255));
            btnPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            btnPanel.add(applyBtn);
            add(btnPanel, BorderLayout.SOUTH);

            refresh();
        }

        private void refresh() {
            try {
                model.setRowCount(0);
                List<Database.JobMatchRecord> matches = Database.getMatchingJobsForApplicant(applicantId);
                for (Database.JobMatchRecord m : matches) {
                    model.addRow(new Object[]{
                        m.jobId,
                        m.title,
                        String.format("₱%,.2f", m.salary),
                        m.matchPercentage + "%",
                        "Apply"
                    });
                }
                if (matches.isEmpty()) {
                    model.addRow(new Object[]{"-", "No matching jobs found. Add skills to get recommendations!", "-", "-", "-"});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading recommended jobs: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Panel for managing attendance
    private class ApplicantAttendancePanel extends JPanel {
        private DefaultTableModel model;
        private JTable table;
        private JButton timeInBtn, timeOutBtn;
        private JLabel statusLabel;
        private int applicantId;
        private Database.AttendanceRecord todayAttendance;

        public ApplicantAttendancePanel(int applicantId) {
            this.applicantId = applicantId;
            setLayout(new BorderLayout());
            setBackground(new Color(248, 250, 252));

            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(new Color(255, 255, 255));
            titlePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(20, 25, 20, 25)
            ));
            
            JLabel title = new JLabel("Attendance");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(new Color(40, 80, 200));
            titlePanel.add(title, BorderLayout.WEST);
            
            JLabel subtitle = new JLabel("Track your work attendance - Time in/out to record your working hours");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(120, 120, 120));
            titlePanel.add(subtitle, BorderLayout.SOUTH);
            
            add(titlePanel, BorderLayout.NORTH);

            // Time in/out panel
            JPanel timePanel = new JPanel(new GridBagLayout());
            timePanel.setBackground(new Color(255, 255, 255));
            timePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(25, 25, 25, 25)
            ));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 15, 10, 15);
            
            timeInBtn = ApplicantGUI.this.createModernButton("Time In", new Color(74, 144, 226));  // Muted Blue #4A90E2
            timeInBtn.setPreferredSize(new Dimension(200, 50));
            timeInBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            timeInBtn.addActionListener(e -> doTimeIn());
            
            timeOutBtn = ApplicantGUI.this.createModernButton("Time Out", new Color(220, 80, 80));
            timeOutBtn.setPreferredSize(new Dimension(200, 50));
            timeOutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            timeOutBtn.addActionListener(e -> doTimeOut());
            
            statusLabel = new JLabel("Status: Not checked in today");
            statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            statusLabel.setForeground(new Color(100, 100, 100));
            
            gbc.gridx = 0; gbc.gridy = 0;
            timePanel.add(timeInBtn, gbc);
            gbc.gridx = 1;
            timePanel.add(timeOutBtn, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.gridwidth = 2;
            timePanel.add(statusLabel, gbc);
            
            add(timePanel, BorderLayout.CENTER);

            // Attendance history table
            model = new DefaultTableModel(new String[]{"Date", "Time In", "Time Out", "Hours Worked", "Status"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            table = new JTable(model);
            table.setRowHeight(30);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            table.getTableHeader().setBackground(new Color(60, 130, 255));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getColumnModel().getColumn(0).setPreferredWidth(120);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(3).setPreferredWidth(120);
            table.getColumnModel().getColumn(4).setPreferredWidth(100);
            
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(new Color(255, 255, 255));
            tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25));
            JLabel tableTitle = new JLabel("Attendance History");
            tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
            tableTitle.setForeground(new Color(40, 80, 200));
            tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            tablePanel.add(tableTitle, BorderLayout.NORTH);
            tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
            
            add(tablePanel, BorderLayout.SOUTH);
            
            refresh();
        }

        private void doTimeIn() {
            try {
                Database.AttendanceRecord today = Database.getTodayAttendance(applicantId);
                if (today != null && today.timeIn != null) {
                    JOptionPane.showMessageDialog(this, 
                        "You have already timed in today at " + today.timeIn.substring(0, 5),
                        "Already Timed In",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                Database.recordTimeIn(applicantId);
                JOptionPane.showMessageDialog(this, 
                    "Time in recorded successfully!\nTime: " + java.time.LocalTime.now().toString().substring(0, 5),
                    "Time In",
                    JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error recording time in: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        private void doTimeOut() {
            try {
                Database.AttendanceRecord today = Database.getTodayAttendance(applicantId);
                if (today == null || today.timeIn == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Please time in first before timing out.",
                        "No Time In",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (today.timeOut != null) {
                    JOptionPane.showMessageDialog(this, 
                        "You have already timed out today at " + today.timeOut.substring(0, 5),
                        "Already Timed Out",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                Database.recordTimeOut(applicantId);
                today = Database.getTodayAttendance(applicantId);
                String message = "Time out recorded successfully!\n";
                if (today != null) {
                    message += "Time: " + today.timeOut.substring(0, 5) + "\n";
                    message += "Hours worked: " + String.format("%.2f", today.hoursWorked) + " hours";
                }
                JOptionPane.showMessageDialog(this, message, "Time Out", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error recording time out: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        private void refresh() {
            try {
                todayAttendance = Database.getTodayAttendance(applicantId);
                
                // Update status label
                if (todayAttendance == null || todayAttendance.timeIn == null) {
                    statusLabel.setText("Status: Not checked in today");
                    statusLabel.setForeground(new Color(100, 100, 100));
                    timeInBtn.setEnabled(true);
                    timeOutBtn.setEnabled(false);
                } else if (todayAttendance.timeOut == null) {
                    statusLabel.setText("Status: Checked in at " + todayAttendance.timeIn.substring(0, 5) + " - Still working");
                    statusLabel.setForeground(new Color(60, 180, 80));
                    timeInBtn.setEnabled(false);
                    timeOutBtn.setEnabled(true);
                } else {
                    statusLabel.setText("Status: Completed - " + String.format("%.2f", todayAttendance.hoursWorked) + " hours worked today");
                    statusLabel.setForeground(new Color(100, 100, 100));
                    timeInBtn.setEnabled(false);
                    timeOutBtn.setEnabled(false);
                }
                
                // Refresh table
                model.setRowCount(0);
                List<Database.AttendanceRecord> attendance = Database.getApplicantAttendance(applicantId);
                for (Database.AttendanceRecord att : attendance) {
                    String timeIn = att.timeIn != null ? att.timeIn.substring(0, 5) : "-";
                    String timeOut = att.timeOut != null ? att.timeOut.substring(0, 5) : "-";
                    String hours = att.hoursWorked > 0 ? String.format("%.2f", att.hoursWorked) : "-";
                    String status = att.status != null ? att.status : "-";
                    model.addRow(new Object[]{att.date, timeIn, timeOut, hours, status});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading attendance: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Panel for training programs
    private class ApplicantTrainingPanel extends JPanel {
        private DefaultTableModel model;
        private JTable table;
        private int applicantId;

        public ApplicantTrainingPanel(int applicantId) {
            this.applicantId = applicantId;
            setLayout(new BorderLayout());
            setBackground(new Color(248, 250, 252));

            JPanel titlePanel = new JPanel(new BorderLayout());
            titlePanel.setBackground(new Color(255, 255, 255));
            titlePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(20, 25, 20, 25)
            ));
            
            JLabel title = new JLabel("Available Training Programs");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(new Color(40, 80, 200));
            titlePanel.add(title, BorderLayout.WEST);
            
            JLabel subtitle = new JLabel("Enroll in training programs to improve your skills and job prospects");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(120, 120, 120));
            titlePanel.add(subtitle, BorderLayout.SOUTH);
            
            add(titlePanel, BorderLayout.NORTH);

            model = new DefaultTableModel(new String[]{"Program ID", "Title", "Duration (Days)", "Skills Taught", "Action"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            table = new JTable(model);
            table.setRowHeight(30);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            table.getTableHeader().setBackground(new Color(60, 130, 255));
            table.getTableHeader().setForeground(Color.WHITE);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JButton enrollBtn = ApplicantGUI.this.createModernButton("Enroll in Selected Program", new Color(74, 144, 226));  // Muted Blue #4A90E2
            enrollBtn.setPreferredSize(new Dimension(250, 45));
            enrollBtn.addActionListener(e -> {
                int r = table.getSelectedRow();
                if (r == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a training program.");
                    return;
                }
                int programId = (int) model.getValueAt(r, 0);
                try {
                    Database.enrollInTraining(applicantId, programId);
                    JOptionPane.showMessageDialog(this, "Successfully enrolled in training program!");
                    refresh();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });

            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(new Color(255, 255, 255));
            btnPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(230, 235, 240)),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            btnPanel.add(enrollBtn);
            add(btnPanel, BorderLayout.SOUTH);

            refresh();
        }

        private void refresh() {
            try {
                model.setRowCount(0);
                List<Database.TrainingProgramRecord> programs = Database.getAllTrainingPrograms();
                for (Database.TrainingProgramRecord p : programs) {
                    if ("ACTIVE".equals(p.status)) {
                        String skills = p.skillsTaught != null ? p.skillsTaught : "";
                        String shortSkills = skills.length() > 40 ? skills.substring(0, 40) + "..." : skills;
                        model.addRow(new Object[]{p.id, p.title != null ? p.title : "N/A", p.durationDays, shortSkills, "Enroll"});
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading training programs: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Animated color-shifting gradient panel for background
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

