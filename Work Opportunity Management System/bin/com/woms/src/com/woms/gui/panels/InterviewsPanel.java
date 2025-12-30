package com.woms.gui.panels;

import com.woms.database.Database;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InterviewsPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField dateF, timeF, notesF;
    private JButton scheduleBtn, updateStatusBtn;

    public InterviewsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(255, 255, 255));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JLabel title = new JLabel("Interview Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 130, 255));
        titlePanel.add(title, BorderLayout.NORTH);
        
        JLabel subtitle = new JLabel("Schedule and manage interviews for approved applications", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        titlePanel.add(subtitle, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Interview ID","Applicant","Job Title","Interview Date","Interview Time","Status","Notes"},0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
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
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);
        
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 5 && value != null) {
                    String status = value.toString().toUpperCase();
                    if ("COMPLETED".equals(status)) {
                        c.setForeground(new Color(60, 180, 80));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if ("CANCELLED".equals(status)) {
                        c.setForeground(new Color(220, 60, 60));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(60, 130, 255));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
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
        gbc.insets = new Insets(8,8,8,8); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        dateF = new JTextField();
        dateF.setPreferredSize(new Dimension(0, 32));
        dateF.setMinimumSize(new Dimension(0, 32));
        dateF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        dateF.setToolTipText("Format: YYYY-MM-DD (e.g., 2024-01-15)");
        timeF = new JTextField();
        timeF.setPreferredSize(new Dimension(0, 32));
        timeF.setMinimumSize(new Dimension(0, 32));
        timeF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        timeF.setToolTipText("Format: HH:mm (e.g., 14:30)");
        notesF = new JTextField();
        notesF.setPreferredSize(new Dimension(0, 32));
        notesF.setMinimumSize(new Dimension(0, 32));
        notesF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        gbc.gridx=0; gbc.gridy=0; form.add(label("Interview Date (YYYY-MM-DD):"), gbc);
        gbc.gridx=1; form.add(dateF, gbc);
        gbc.gridx=0; gbc.gridy=1; form.add(label("Interview Time (HH:mm):"), gbc);
        gbc.gridx=1; form.add(timeF, gbc);
        gbc.gridx=0; gbc.gridy=2; form.add(label("Notes:"), gbc);
        gbc.gridx=1; form.add(notesF, gbc);

        updateStatusBtn = createButton("Update Selected Status", new Color(60,180,80));
        JButton refreshBtn = createButton("Refresh", new Color(100, 120, 150));

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bp.setBackground(new Color(255, 255, 255));
        bp.add(updateStatusBtn);
        bp.add(refreshBtn);

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; form.add(bp, gbc);
        add(form, BorderLayout.SOUTH);

        updateStatusBtn.addActionListener(e -> {
            int r = table.getSelectedRow(); 
            if (r==-1) { 
                JOptionPane.showMessageDialog(this,"Please select an interview to update."); 
                return; 
            }
            int interviewId = (int) model.getValueAt(r,0);
            String[] options = {"SCHEDULED", "COMPLETED", "CANCELLED", "NO_SHOW"};
            String newStatus = (String) JOptionPane.showInputDialog(this,
                "Select new status:",
                "Update Interview Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            if (newStatus != null) {
                try {
                    Database.updateInterviewStatus(interviewId, newStatus);
                    refresh();
                    JOptionPane.showMessageDialog(this, "Interview status updated to: " + newStatus);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        refreshBtn.addActionListener(e -> refresh());

        refresh();
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            List<Database.InterviewRecord> list = Database.getAllInterviews();
            
            if (list == null || list.isEmpty()) {
                model.addRow(new Object[]{"-", "No interviews scheduled", "-", "-", "-", "-", "-"});
                return;
            }
            
            for (Database.InterviewRecord i : list) {
                String applicantName = i.applicantName != null ? i.applicantName : 
                                      (i.applicantUsername != null ? i.applicantUsername : "N/A");
                String jobTitle = i.jobTitle != null ? i.jobTitle : "N/A";
                String date = i.interviewDate != null ? i.interviewDate : "N/A";
                String time = i.interviewTime != null ? i.interviewTime : "N/A";
                String status = i.status != null ? i.status : "SCHEDULED";
                String notes = i.notes != null && !i.notes.isEmpty() ? 
                              (i.notes.length() > 50 ? i.notes.substring(0, 50) + "..." : i.notes) : "No notes";
                
                model.addRow(new Object[]{
                    i.id,
                    applicantName,
                    jobTitle,
                    date,
                    time,
                    status,
                    notes
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading interviews: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
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
