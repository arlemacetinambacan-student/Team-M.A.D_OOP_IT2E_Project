package com.woms.gui.panels;

import com.woms.database.Database;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class JobsPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField titleF, descF, salaryF, skillsF;
    private JButton addBtn, deleteBtn;

    public JobsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(255, 255, 255));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JLabel title = new JLabel("Job Opportunities Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 130, 255));
        titlePanel.add(title, BorderLayout.NORTH);
        
        JLabel subtitle = new JLabel("Create employment opportunities to help reduce poverty", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        titlePanel.add(subtitle, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"JobID","Title","Description","Salary"},0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(60, 130, 255, 50));
        table.setSelectionForeground(new Color(60, 130, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(60, 130, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 40));
        JScrollPane sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 235), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        add(sc, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        form.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8); gbc.fill = GridBagConstraints.HORIZONTAL;

        titleF = new JTextField();
        titleF.setPreferredSize(new Dimension(0, 32));
        titleF.setMinimumSize(new Dimension(0, 32));
        titleF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        descF = new JTextField();
        descF.setPreferredSize(new Dimension(0, 32));
        descF.setMinimumSize(new Dimension(0, 32));
        descF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        descF.setToolTipText("Describe how this job helps reduce poverty and provides income opportunities (e.g., 'Entry-level position, training provided, helps families earn stable income')");
        salaryF = new JTextField();
        salaryF.setPreferredSize(new Dimension(0, 32));
        salaryF.setMinimumSize(new Dimension(0, 32));
        salaryF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        skillsF = new JTextField();
        skillsF.setPreferredSize(new Dimension(0, 32));
        skillsF.setMinimumSize(new Dimension(0, 32));
        skillsF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        skillsF.setToolTipText("Enter required skills separated by commas (e.g., Physical Strength, Basic Math, Communication, Driving License)");

        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.2; form.add(label("Title:"), gbc);
        gbc.gridx=1; gbc.weightx=0.8; form.add(titleF, gbc);
        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.2; form.add(label("Description:"), gbc);
        gbc.gridx=1; gbc.weightx=0.8; form.add(descF, gbc);
        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.2; form.add(label("Salary:"), gbc);
        gbc.gridx=1; gbc.weightx=0.8; form.add(salaryF, gbc);
        gbc.gridx=0; gbc.gridy=3; gbc.weightx=0.2; form.add(label("Required Skills:"), gbc);
        gbc.gridx=1; gbc.weightx=0.8; form.add(skillsF, gbc);

        addBtn = createButton("Add Job", new Color(60,130,255));
        deleteBtn = createButton("Delete Selected", new Color(200,60,70));

        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; gbc.weightx=1.0; gbc.anchor=GridBagConstraints.CENTER; form.add(buttonPanel(), gbc);

        add(form, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            try {
                String t = titleF.getText().trim(); String d = descF.getText().trim();
                double s = Double.parseDouble(salaryF.getText().trim());
                String skills = skillsF.getText().trim();
                if (t.isEmpty()) { JOptionPane.showMessageDialog(this,"Title required"); return; }
                int jobId = Database.createJob(t,d,s);
                // Add required skills if provided
                if (!skills.isEmpty()) {
                    String[] skillArray = skills.split(",");
                    for (String skill : skillArray) {
                        String skillTrimmed = skill.trim();
                        if (!skillTrimmed.isEmpty()) {
                            Database.addJobRequiredSkill(jobId, skillTrimmed);
                        }
                    }
                }
                refresh();
                JOptionPane.showMessageDialog(this,"Job added successfully!");
                titleF.setText(""); descF.setText(""); salaryF.setText(""); skillsF.setText("");
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this,"Salary must be a number"); }
            catch (SQLException ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });

        deleteBtn.addActionListener(e -> {
            int r = table.getSelectedRow(); if (r==-1) { JOptionPane.showMessageDialog(this,"Select row"); return; }
            int id = (int) model.getValueAt(r,0);
            try { Database.deleteJob(id); refresh(); JOptionPane.showMessageDialog(this,"Deleted."); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });

        refresh();
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            List<Database.JobRecord> list = Database.getAllJobs();
            for (Database.JobRecord j : list) model.addRow(new Object[]{j.id,j.title,j.description,j.salary});
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading jobs: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buttonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        p.setBackground(new Color(255, 255, 255));
        p.add(addBtn); p.add(deleteBtn);
        return p;
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
        b.setPreferredSize(new Dimension(150, 45));
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
