package com.woms.gui.panels;

import com.woms.database.Database;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ApplicationsPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField applicantF, jobF;
    private JButton submitBtn, approveBtn, rejectBtn;

    public ApplicationsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(255, 255, 255));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JLabel title = new JLabel("Job Applications Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 130, 255));
        titlePanel.add(title, BorderLayout.NORTH);
        
        JLabel subtitle = new JLabel("View and manage all job applications from applicants", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        titlePanel.add(subtitle, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"App ID","Applicant Username","Applicant Name","Job Title","Age","Gender","Status","Date Submitted","View Details"},0) {
            @Override public boolean isCellEditable(int r,int c){ return c == 8; }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(60, 130, 255, 50));
        table.setSelectionForeground(new Color(60, 130, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(60, 130, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 40));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // App ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Username
        table.getColumnModel().getColumn(2).setPreferredWidth(150);  // Name
        table.getColumnModel().getColumn(3).setPreferredWidth(150);  // Job Title
        table.getColumnModel().getColumn(4).setPreferredWidth(50);  // Age
        table.getColumnModel().getColumn(5).setPreferredWidth(80);   // Gender
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Status
        table.getColumnModel().getColumn(7).setPreferredWidth(150); // Date Submitted
        table.getColumnModel().getColumn(8).setPreferredWidth(100); // View Details
        
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 6 && value != null) {
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
        
        // Add button renderer for View Details
        table.getColumn("View Details").setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JButton btn = new JButton("View Details");
                btn.setBackground(new Color(60, 130, 255));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btn.setFocusPainted(false);
                return btn;
            }
        });
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 8) {
                    viewApplicationDetails(row);
                }
            }
        });
        
        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 235), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        add(sc, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(255, 255, 255));
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8); gbc.fill = GridBagConstraints.HORIZONTAL;

        applicantF = new JTextField();
        applicantF.setPreferredSize(new Dimension(0, 32));
        applicantF.setMinimumSize(new Dimension(0, 32));
        applicantF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        jobF = new JTextField();
        jobF.setPreferredSize(new Dimension(0, 32));
        jobF.setMinimumSize(new Dimension(0, 32));
        jobF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        gbc.gridx=0; gbc.gridy=0; form.add(label("Applicant ID:"), gbc);
        gbc.gridx=1; form.add(applicantF, gbc);
        gbc.gridx=0; gbc.gridy=1; form.add(label("Job ID:"), gbc);
        gbc.gridx=1; form.add(jobF, gbc);

        submitBtn = createButton("Submit Application", new Color(60,130,255));
        approveBtn = createButton("Approve Selected", new Color(60,180,80));
        rejectBtn = createButton("Reject Selected", new Color(200,60,70));

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bp.setBackground(new Color(255, 255, 255));
        bp.add(submitBtn); bp.add(approveBtn); bp.add(rejectBtn);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; form.add(bp, gbc);
        add(form, BorderLayout.SOUTH);

        submitBtn.addActionListener(e -> {
            try {
                int aid = Integer.parseInt(applicantF.getText().trim());
                int jid = Integer.parseInt(jobF.getText().trim());
                Database.createApplication(aid,jid);
                refresh();
                JOptionPane.showMessageDialog(this,"Application submitted.");
                applicantF.setText(""); jobF.setText("");
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this,"IDs must be numbers."); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });

        approveBtn.addActionListener(e -> {
            int r = table.getSelectedRow(); 
            if (r==-1) { 
                JOptionPane.showMessageDialog(this,"Please select an application to approve."); 
                return; 
            }
            int appId = (int) model.getValueAt(r,0);
            try {
                // Get application details
                List<Database.ApplicationRecord> apps = Database.getAllApplications();
                Database.ApplicationRecord app = null;
                for (Database.ApplicationRecord a : apps) {
                    if (a.id == appId) {
                        app = a;
                        break;
                    }
                }
                
                if (app == null) {
                    JOptionPane.showMessageDialog(this, "Application not found.");
                    return;
                }
                
                // Check if interview already exists
                Database.InterviewRecord existingInterview = Database.getInterviewByApplicationId(appId);
                if (existingInterview != null) {
                    JOptionPane.showMessageDialog(this, 
                        "Interview already scheduled for this application.\n" +
                        "Interview ID: " + existingInterview.id + "\n" +
                        "Status: " + existingInterview.status,
                        "Interview Exists",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                // Update application status
                Database.updateApplicationStatus(appId, "APPROVED");
                
                // Schedule interview - default to 3 days from now
                java.time.LocalDate interviewDate = java.time.LocalDate.now().plusDays(3);
                String interviewDateStr = interviewDate.toString();
                String interviewTimeStr = "10:00"; // Default time
                
                // Create interview
                int interviewId = Database.createInterview(
                    appId, 
                    app.applicantId, 
                    app.jobId, 
                    interviewDateStr, 
                    interviewTimeStr, 
                    "Interview scheduled automatically upon application approval."
                );
                
                refresh();
                JOptionPane.showMessageDialog(this, 
                    "Application approved successfully!\n\n" +
                    "Interview automatically scheduled:\n" +
                    "Interview ID: " + interviewId + "\n" +
                    "Date: " + interviewDateStr + "\n" +
                    "Time: " + interviewTimeStr + "\n\n" +
                    "You can manage interviews in the Interviews section.",
                    "Approved & Interview Scheduled",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) { 
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); 
            }
        });

        rejectBtn.addActionListener(e -> {
            int r = table.getSelectedRow(); if (r==-1) { JOptionPane.showMessageDialog(this,"Select app"); return; }
            int appId = (int) model.getValueAt(r,0);
            try { Database.updateApplicationStatus(appId,"REJECTED"); refresh(); JOptionPane.showMessageDialog(this,"Rejected."); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });

        refresh();
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            List<Database.ApplicationRecord> list = Database.getAllApplications();
            
            if (list == null || list.isEmpty()) {
                model.addRow(new Object[]{"-", "No applications found", "-", "-", "-", "-", "-", "-", "-"});
                return;
            }
            
            for (Database.ApplicationRecord a : list) {
                // Build full name from first, middle, last name
                String fullName = (a.firstName != null ? a.firstName : "") + " " + 
                                 (a.middleName != null && !a.middleName.isEmpty() ? a.middleName + " " : "") + 
                                 (a.lastName != null ? a.lastName : "");
                fullName = fullName.trim();
                // Fallback to applicantName if full name is empty
                if (fullName.isEmpty() && a.applicantName != null) {
                    fullName = a.applicantName;
                }
                if (fullName.isEmpty()) {
                    fullName = "N/A";
                }
                
                // Get username
                String username = a.applicantUsername != null ? a.applicantUsername : "N/A";
                
                // Get age
                String ageStr = a.age > 0 ? String.valueOf(a.age) : "N/A";
                
                // Get gender
                String gender = a.gender != null && !a.gender.isEmpty() ? a.gender : "N/A";
                
                // Get job title
                String jobTitle = a.jobTitle != null ? a.jobTitle : "N/A";
                
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
                
                model.addRow(new Object[]{
                    a.id, 
                    username, 
                    fullName, 
                    jobTitle, 
                    ageStr, 
                    gender, 
                    a.status != null ? a.status : "PENDING",
                    dateStr,
                    "View"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // Debug
            JOptionPane.showMessageDialog(this, 
                "Error loading applications: " + ex.getMessage() + "\n\nPlease check database connection.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            model.setRowCount(0);
            model.addRow(new Object[]{"-", "Error loading applications", "-", "-", "-", "-", "-", "-", "-"});
        } catch (Exception ex) {
            ex.printStackTrace(); // Debug
            JOptionPane.showMessageDialog(this, 
                "Unexpected error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewApplicationDetails(int row) {
        try {
            int appId = (int) model.getValueAt(row, 0);
            List<Database.ApplicationRecord> list = Database.getAllApplications();
            Database.ApplicationRecord app = null;
            for (Database.ApplicationRecord a : list) {
                if (a.id == appId) {
                    app = a;
                    break;
                }
            }
            
            if (app != null) {
                StringBuilder details = new StringBuilder();
                details.append("═══════════════════════════════════════\n");
                details.append("   JOB APPLICATION DETAILS\n");
                details.append("═══════════════════════════════════════\n\n");
                
                details.append("APPLICATION INFORMATION:\n");
                details.append("─────────────────────────────\n");
                details.append("Application ID: ").append(app.id).append("\n");
                details.append("Job Applied For: ").append(app.jobTitle != null ? app.jobTitle : "N/A").append("\n");
                details.append("Status: ").append(app.status).append("\n\n");
                
                details.append("APPLICANT INFORMATION:\n");
                details.append("─────────────────────────────\n");
                details.append("Username: ").append(app.applicantUsername != null ? app.applicantUsername : "N/A").append("\n");
                details.append("Full Name: ").append(app.applicantName != null ? app.applicantName : "N/A").append("\n");
                details.append("First Name: ").append(app.firstName != null ? app.firstName : "N/A").append("\n");
                details.append("Middle Name: ").append(app.middleName != null && !app.middleName.isEmpty() ? app.middleName : "N/A").append("\n");
                details.append("Last Name: ").append(app.lastName != null ? app.lastName : "N/A").append("\n");
                details.append("Gender: ").append(app.gender != null ? app.gender : "N/A").append("\n");
                details.append("Age: ").append(app.age > 0 ? app.age : "N/A").append("\n");
                details.append("Address: ").append(app.address != null ? app.address : "N/A").append("\n\n");
                
                details.append("WORK EXPERIENCE:\n");
                details.append("─────────────────────────────\n");
                details.append(app.experience != null && !app.experience.isEmpty() ? app.experience : "No experience provided");
                
                JTextArea textArea = new JTextArea(details.toString());
                textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
                textArea.setEditable(false);
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Application Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading details: " + ex.getMessage());
        }
    }

    private JLabel label(String t) { 
        JLabel l = new JLabel(t); 
        l.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        l.setForeground(new Color(60, 60, 60));
        return l; 
    }
    private JButton createButton(String text, Color color) {
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
        b.setPreferredSize(new Dimension(160, 45));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
}
