package com.woms.gui.panels;

import com.woms.database.Database;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EmploymentStatusPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField applicantIdF, jobIdF, incomeF;
    private JButton addBtn, updateBtn;
    private JComboBox<String> statusCombo;

    public EmploymentStatusPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        JLabel title = new JLabel("Employment Status & Income Tracking", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 130, 255));
        title.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        add(title, BorderLayout.NORTH);

        // Statistics Panel
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Applicant Name", "Job Title", "Start Date", "Monthly Income (₱)", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
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
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 5 && value != null) {
                    String status = value.toString().toUpperCase();
                    if ("EMPLOYED".equals(status)) {
                        c.setForeground(new Color(60, 180, 80));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(220, 60, 60));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(255, 255, 255));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        applicantIdF = new JTextField();
        applicantIdF.setPreferredSize(new Dimension(0, 32));
        applicantIdF.setMinimumSize(new Dimension(0, 32));
        applicantIdF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        jobIdF = new JTextField();
        jobIdF.setPreferredSize(new Dimension(0, 32));
        jobIdF.setMinimumSize(new Dimension(0, 32));
        jobIdF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        incomeF = new JTextField();
        incomeF.setPreferredSize(new Dimension(0, 32));
        incomeF.setMinimumSize(new Dimension(0, 32));
        incomeF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        statusCombo = new JComboBox<>(new String[]{"EMPLOYED", "TERMINATED", "RESIGNED"});

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(label("Applicant ID:"), gbc);
        gbc.gridx = 1;
        form.add(applicantIdF, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(label("Job ID:"), gbc);
        gbc.gridx = 1;
        form.add(jobIdF, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(label("Monthly Income (₱):"), gbc);
        gbc.gridx = 1;
        form.add(incomeF, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(label("Status:"), gbc);
        gbc.gridx = 1;
        form.add(statusCombo, gbc);

        addBtn = createButton("Add Employment Record", new Color(60, 180, 80));
        updateBtn = createButton("Update Selected", new Color(60, 130, 255));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(new Color(255, 255, 255));
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        add(form, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            try {
                int applicantId = Integer.parseInt(applicantIdF.getText().trim());
                int jobId = Integer.parseInt(jobIdF.getText().trim());
                double income = Double.parseDouble(incomeF.getText().trim());
                Database.createEmploymentStatus(applicantId, jobId, income);
                refresh();
                JOptionPane.showMessageDialog(this, "Employment record added!");
                clearForm();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        refresh();
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            List<Database.EmploymentStatusRecord> all = Database.getAllEmploymentStatus();
            long employed = all.stream().filter(e -> "EMPLOYED".equals(e.status)).count();
            double totalIncome = all.stream().filter(e -> "EMPLOYED".equals(e.status)).mapToDouble(e -> e.monthlyIncome).sum();
            double avgIncome = employed > 0 ? totalIncome / employed : 0;

            panel.add(createStatCard("Total Employed", String.valueOf(employed), new Color(60, 180, 80)));
            panel.add(createStatCard("Total Monthly Income", String.format("₱%,.2f", totalIncome), new Color(60, 130, 255)));
            panel.add(createStatCard("Average Income", String.format("₱%,.2f", avgIncome), new Color(255, 165, 0)));
            panel.add(createStatCard("Total Records", String.valueOf(all.size()), new Color(150, 150, 150)));
        } catch (SQLException ex) {
            panel.add(createStatCard("Error", "Loading...", new Color(220, 60, 60)));
        }

        return panel;
    }

    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel labelL = new JLabel(label);
        labelL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelL.setForeground(Color.WHITE);
        card.add(labelL, BorderLayout.NORTH);

        JLabel valueL = new JLabel(value);
        valueL.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueL.setForeground(Color.WHITE);
        card.add(valueL, BorderLayout.CENTER);

        return card;
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            List<Database.EmploymentStatusRecord> list = Database.getAllEmploymentStatus();
            for (Database.EmploymentStatusRecord e : list) {
                String date = e.startDate != null && e.startDate.length() > 10 ? e.startDate.substring(0, 10) : (e.startDate != null ? e.startDate : "N/A");
                model.addRow(new Object[]{
                    e.id,
                    e.applicantName != null ? e.applicantName : "N/A",
                    e.jobTitle != null ? e.jobTitle : "N/A",
                    date,
                    String.format("₱%,.2f", e.monthlyIncome),
                    e.status
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading employment status: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        applicantIdF.setText("");
        jobIdF.setText("");
        incomeF.setText("");
        statusCombo.setSelectedIndex(0);
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
        b.setPreferredSize(new Dimension(180, 45));
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

