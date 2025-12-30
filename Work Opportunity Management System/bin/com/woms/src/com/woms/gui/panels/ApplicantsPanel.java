package com.woms.gui.panels;

import com.woms.database.Database;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ApplicantsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JButton deleteBtn;

    public ApplicantsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        JLabel title = new JLabel("Applicant Registration & Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(20, 12, 20, 12));
        title.setForeground(new Color(60, 130, 255));
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"UserID","Username","Name","Age","Phone"};
        model = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r,int c) { return false; }
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

        // Delete Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBorder(new EmptyBorder(20, 16, 20, 16));
        btnPanel.setBackground(new Color(255, 255, 255));
        
        deleteBtn = createButton("Delete Selected", new Color(220, 60, 70));
        btnPanel.add(deleteBtn);
        
        add(btnPanel, BorderLayout.SOUTH);

        // actions
        deleteBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r==-1) { JOptionPane.showMessageDialog(this,"Select row"); return; }
            int uid = (int) model.getValueAt(r,0);
            try { Database.deleteApplicant(uid); refresh(); JOptionPane.showMessageDialog(this,"Deleted."); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        });

        refresh();
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            List<Database.ApplicantRecord> list = Database.getAllApplicants();
            for (Database.ApplicantRecord a : list) {
                model.addRow(new Object[]{a.userId,a.username,a.name,a.age,a.phone});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading applicants: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
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
