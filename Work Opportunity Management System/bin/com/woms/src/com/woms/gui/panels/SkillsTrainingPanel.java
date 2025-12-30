package com.woms.gui.panels;

import com.woms.database.Database;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SkillsTrainingPanel extends JPanel {
    private DefaultTableModel programModel, enrollmentModel;
    private JTable programTable, enrollmentTable;
    private JTextField titleF, descF, durationF, skillsF;
    private JButton addProgramBtn, deleteProgramBtn;

    public SkillsTrainingPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        // Title
        JLabel title = new JLabel("Skills & Training Programs", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 130, 255));
        title.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        add(title, BorderLayout.NORTH);

        // Split pane for programs and enrollments
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createProgramsPanel());
        splitPane.setRightComponent(createEnrollmentsPanel());
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createProgramsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel subTitle = new JLabel("Training Programs");
        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subTitle.setForeground(new Color(60, 60, 60));
        panel.add(subTitle, BorderLayout.NORTH);

        programModel = new DefaultTableModel(new String[]{"ID", "Title", "Duration (Days)", "Skills Taught", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        programTable = new JTable(programModel);
        programTable.setRowHeight(30);
        programTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        programTable.getTableHeader().setBackground(new Color(60, 130, 255));
        programTable.getTableHeader().setForeground(Color.WHITE);
        panel.add(new JScrollPane(programTable), BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(248, 250, 252));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        titleF = new JTextField();
        titleF.setPreferredSize(new Dimension(0, 32));
        titleF.setMinimumSize(new Dimension(0, 32));
        titleF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        descF = new JTextField();
        descF.setPreferredSize(new Dimension(0, 32));
        descF.setMinimumSize(new Dimension(0, 32));
        descF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        durationF = new JTextField();
        durationF.setPreferredSize(new Dimension(0, 32));
        durationF.setMinimumSize(new Dimension(0, 32));
        durationF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        skillsF = new JTextField();
        skillsF.setPreferredSize(new Dimension(0, 32));
        skillsF.setMinimumSize(new Dimension(0, 32));
        skillsF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(label("Title:"), gbc);
        gbc.gridx = 1;
        form.add(titleF, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(label("Description:"), gbc);
        gbc.gridx = 1;
        form.add(descF, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(label("Duration (Days):"), gbc);
        gbc.gridx = 1;
        form.add(durationF, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(label("Skills Taught:"), gbc);
        gbc.gridx = 1;
        form.add(skillsF, gbc);

        addProgramBtn = createButton("Add Program", new Color(60, 180, 80));
        deleteProgramBtn = createButton("Delete Selected", new Color(220, 60, 60));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(new Color(248, 250, 252));
        btnPanel.add(addProgramBtn);
        btnPanel.add(deleteProgramBtn);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        panel.add(form, BorderLayout.SOUTH);

        addProgramBtn.addActionListener(e -> {
            try {
                String title = titleF.getText().trim();
                String desc = descF.getText().trim();
                int duration = Integer.parseInt(durationF.getText().trim());
                String skills = skillsF.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title required");
                    return;
                }
                Database.createTrainingProgram(title, desc, duration, skills);
                refreshPrograms();
                JOptionPane.showMessageDialog(this, "Training program added!");
                titleF.setText("");
                descF.setText("");
                durationF.setText("");
                skillsF.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Duration must be a number");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        deleteProgramBtn.addActionListener(e -> {
            int r = programTable.getSelectedRow();
            if (r == -1) {
                JOptionPane.showMessageDialog(this, "Select a program to delete");
                return;
            }
            int programId = (int) programModel.getValueAt(r, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this training program?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Database.deleteTrainingProgram(programId);
                    refreshPrograms();
                    JOptionPane.showMessageDialog(this, "Training program deleted!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        refreshPrograms();
        return panel;
    }

    private JPanel createEnrollmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel subTitle = new JLabel("Training Enrollments");
        subTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subTitle.setForeground(new Color(60, 60, 60));
        panel.add(subTitle, BorderLayout.NORTH);

        enrollmentModel = new DefaultTableModel(new String[]{"Applicant", "Program", "Enrollment Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        enrollmentTable = new JTable(enrollmentModel);
        enrollmentTable.setRowHeight(30);
        enrollmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        enrollmentTable.getTableHeader().setBackground(new Color(60, 130, 255));
        enrollmentTable.getTableHeader().setForeground(Color.WHITE);
        panel.add(new JScrollPane(enrollmentTable), BorderLayout.CENTER);

        // Would need to add method to get all enrollments
        refreshEnrollments();
        return panel;
    }

    private void refreshPrograms() {
        try {
            programModel.setRowCount(0);
            List<Database.TrainingProgramRecord> programs = Database.getAllTrainingPrograms();
            for (Database.TrainingProgramRecord p : programs) {
                programModel.addRow(new Object[]{
                    p.id, 
                    p.title != null ? p.title : "N/A", 
                    p.durationDays, 
                    p.skillsTaught != null ? p.skillsTaught : "N/A", 
                    p.status != null ? p.status : "N/A"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading training programs: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshEnrollments() {
        try {
            enrollmentModel.setRowCount(0);
            List<Database.TrainingEnrollmentRecord> enrollments = Database.getAllTrainingEnrollments();
            for (Database.TrainingEnrollmentRecord e : enrollments) {
                String date = e.enrollmentDate != null && e.enrollmentDate.length() > 10 ? 
                    e.enrollmentDate.substring(0, 10) : (e.enrollmentDate != null ? e.enrollmentDate : "N/A");
                enrollmentModel.addRow(new Object[]{
                    e.applicantUsername != null ? e.applicantUsername : "N/A",
                    e.programTitle != null ? e.programTitle : "N/A",
                    date,
                    e.status != null ? e.status : "N/A"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading enrollments: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

