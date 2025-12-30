package com.woms.gui.panels;

import com.woms.database.Database;


// ReportsPanel.java - Enhanced with poverty reduction metrics
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;

public class ReportsPanel extends JPanel {
    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        JLabel title = new JLabel("Reports & Poverty Reduction Metrics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 80, 200));
        title.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(title, BorderLayout.NORTH);

        // Poverty Metrics Panel
        JPanel metricsPanel = createPovertyMetricsPanel();
        add(metricsPanel, BorderLayout.CENTER);

        // Export Panel
        JPanel exportPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        exportPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        exportPanel.setBackground(new Color(248, 250, 252));

        JButton exportApplicants = createButton("Export Applicants", new Color(60, 130, 255));
        exportApplicants.addActionListener(e -> exportApplicants());

        JButton exportApplications = createButton("Export Applications", new Color(60, 130, 255));
        exportApplications.addActionListener(e -> exportApplications());

        JButton exportEmployment = createButton("Export Employment Status", new Color(60, 180, 80));
        exportEmployment.addActionListener(e -> exportEmployment());

        JButton exportTraining = createButton("Export Training Programs", new Color(255, 165, 0));
        exportTraining.addActionListener(e -> exportTraining());

        JButton exportResources = createButton("Export Resources", new Color(60, 130, 255));
        exportResources.addActionListener(e -> exportResources());

        JButton exportPovertyReport = createButton("Generate Poverty Reduction Report", new Color(220, 60, 60));
        exportPovertyReport.addActionListener(e -> generatePovertyReport());

        exportPanel.add(exportApplicants);
        exportPanel.add(exportApplications);
        exportPanel.add(exportEmployment);
        exportPanel.add(exportTraining);
        exportPanel.add(exportResources);
        exportPanel.add(exportPovertyReport);

        add(exportPanel, BorderLayout.SOUTH);
    }

    private JPanel createPovertyMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(248, 250, 252));

        try {
            // Employment metrics
            List<Database.EmploymentStatusRecord> employment = Database.getAllEmploymentStatus();
            long totalEmployed = employment.stream().filter(e -> "EMPLOYED".equals(e.status)).count();
            double totalMonthlyIncome = employment.stream().filter(e -> "EMPLOYED".equals(e.status)).mapToDouble(e -> e.monthlyIncome).sum();
            double avgIncome = totalEmployed > 0 ? totalMonthlyIncome / totalEmployed : 0;

            // Application metrics
            List<Database.ApplicationRecord> applications = Database.getAllApplications();
            long totalApplications = applications.size();
            long approvedApps = applications.stream().filter(a -> "APPROVED".equals(a.status)).count();
            double approvalRate = totalApplications > 0 ? (approvedApps * 100.0 / totalApplications) : 0;

            // Training metrics
            List<Database.TrainingProgramRecord> trainings = Database.getAllTrainingPrograms();
            long activeTrainings = trainings.stream().filter(t -> "ACTIVE".equals(t.status)).count();

            // Applicant metrics
            List<Database.ApplicantRecord> applicants = Database.getAllApplicants();
            long totalApplicants = applicants.size();
            double employmentRate = totalApplicants > 0 ? (totalEmployed * 100.0 / totalApplicants) : 0;

            panel.add(createMetricCard("Total Applicants", String.valueOf(totalApplicants), "Registered beneficiaries", new Color(60, 130, 255)));
            panel.add(createMetricCard("Employed", String.valueOf(totalEmployed), String.format("%.1f%% employment rate", employmentRate), new Color(60, 180, 80)));
            panel.add(createMetricCard("Total Monthly Income", String.format("₱%,.2f", totalMonthlyIncome), "From all employed", new Color(255, 165, 0)));
            panel.add(createMetricCard("Average Income", String.format("₱%,.2f", avgIncome), "Per employed person", new Color(60, 180, 80)));
            panel.add(createMetricCard("Job Applications", String.valueOf(totalApplications), String.format("%.1f%% approval rate", approvalRate), new Color(100, 150, 255)));
            panel.add(createMetricCard("Active Training Programs", String.valueOf(activeTrainings), "Skills development", new Color(255, 140, 0)));

        } catch (SQLException ex) {
            panel.add(createMetricCard("Error", "Loading...", ex.getMessage(), new Color(220, 60, 60)));
        }

        return panel;
    }

    private JPanel createMetricCard(String title, String value, String subtitle, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleL = new JLabel(title);
        titleL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleL.setForeground(new Color(100, 100, 100));
        card.add(titleL, BorderLayout.NORTH);

        JLabel valueL = new JLabel(value);
        valueL.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueL.setForeground(color);
        card.add(valueL, BorderLayout.CENTER);

        JLabel subtitleL = new JLabel(subtitle);
        subtitleL.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleL.setForeground(new Color(150, 150, 150));
        card.add(subtitleL, BorderLayout.SOUTH);

        return card;
    }

    private void exportApplicants() {
        try {
            List<Database.ApplicantRecord> list = Database.getAllApplicants();
            try (PrintWriter pw = new PrintWriter(new FileWriter("applicants.csv"))) {
                pw.println("userId,username,name,age,phone");
                for (Database.ApplicantRecord a : list) {
                    pw.println(a.userId + "," + a.username + "," + safe(a.name) + "," + a.age + "," + safe(a.phone));
                }
            }
            JOptionPane.showMessageDialog(this, "applicants.csv created successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void exportApplications() {
        try {
            List<Database.ApplicationRecord> list = Database.getAllApplications();
            try (PrintWriter pw = new PrintWriter(new FileWriter("applications.csv"))) {
                pw.println("applicationId,applicantId,applicantName,jobId,jobTitle,status");
                for (Database.ApplicationRecord a : list) {
                    pw.println(a.id + "," + a.applicantId + "," + safe(a.applicantName) + "," + a.jobId + "," + safe(a.jobTitle) + "," + a.status);
                }
            }
            JOptionPane.showMessageDialog(this, "applications.csv created successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void exportEmployment() {
        try {
            List<Database.EmploymentStatusRecord> list = Database.getAllEmploymentStatus();
            try (PrintWriter pw = new PrintWriter(new FileWriter("employment_status.csv"))) {
                pw.println("employmentId,applicantId,applicantName,jobId,jobTitle,startDate,monthlyIncome,status");
                for (Database.EmploymentStatusRecord e : list) {
                    pw.println(e.id + "," + e.applicantId + "," + safe(e.applicantName) + "," + e.jobId + "," + safe(e.jobTitle) + "," + safe(e.startDate) + "," + e.monthlyIncome + "," + e.status);
                }
            }
            JOptionPane.showMessageDialog(this, "employment_status.csv created successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void exportTraining() {
        try {
            List<Database.TrainingProgramRecord> list = Database.getAllTrainingPrograms();
            try (PrintWriter pw = new PrintWriter(new FileWriter("training_programs.csv"))) {
                pw.println("programId,title,description,durationDays,skillsTaught,status");
                for (Database.TrainingProgramRecord t : list) {
                    pw.println(t.id + "," + safe(t.title) + "," + safe(t.description) + "," + t.durationDays + "," + safe(t.skillsTaught) + "," + t.status);
                }
            }
            JOptionPane.showMessageDialog(this, "training_programs.csv created successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void exportResources() {
        try {
            List<Database.ResourceRecord> list = Database.getAllResources();
            try (PrintWriter pw = new PrintWriter(new FileWriter("resources.csv"))) {
                pw.println("resourceId,name,quantity");
                for (Database.ResourceRecord r : list) {
                    pw.println(r.id + "," + safe(r.name) + "," + r.quantity);
                }
            }
            JOptionPane.showMessageDialog(this, "resources.csv created successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void generatePovertyReport() {
        try {
            List<Database.ApplicantRecord> applicants = Database.getAllApplicants();
            List<Database.EmploymentStatusRecord> employment = Database.getAllEmploymentStatus();
            List<Database.ApplicationRecord> applications = Database.getAllApplications();
            List<Database.TrainingProgramRecord> trainings = Database.getAllTrainingPrograms();

            long totalApplicants = applicants.size();
            long totalEmployed = employment.stream().filter(e -> "EMPLOYED".equals(e.status)).count();
            double totalIncome = employment.stream().filter(e -> "EMPLOYED".equals(e.status)).mapToDouble(e -> e.monthlyIncome).sum();
            double avgIncome = totalEmployed > 0 ? totalIncome / totalEmployed : 0;
            double employmentRate = totalApplicants > 0 ? (totalEmployed * 100.0 / totalApplicants) : 0;
            long approvedApps = applications.stream().filter(a -> "APPROVED".equals(a.status)).count();
            long activeTrainings = trainings.stream().filter(t -> "ACTIVE".equals(t.status)).count();

            try (PrintWriter pw = new PrintWriter(new FileWriter("poverty_reduction_report.txt"))) {
                pw.println("==========================================");
                pw.println("POVERTY REDUCTION REPORT");
                pw.println("Work Opportunity Management System");
                pw.println("==========================================");
                pw.println();
                pw.println("OVERALL METRICS:");
                pw.println("  Total Registered Beneficiaries: " + totalApplicants);
                pw.println("  Currently Employed: " + totalEmployed);
                pw.println("  Employment Rate: " + String.format("%.2f%%", employmentRate));
                pw.println();
                pw.println("INCOME METRICS:");
                pw.println("  Total Monthly Income Generated: ₱" + String.format("%,.2f", totalIncome));
                pw.println("  Average Monthly Income: ₱" + String.format("%,.2f", avgIncome));
                pw.println("  Estimated Annual Income: ₱" + String.format("%,.2f", totalIncome * 12));
                pw.println();
                pw.println("JOB OPPORTUNITIES:");
                pw.println("  Total Applications: " + applications.size());
                pw.println("  Approved Applications: " + approvedApps);
                pw.println("  Approval Rate: " + String.format("%.2f%%", applications.size() > 0 ? (approvedApps * 100.0 / applications.size()) : 0));
                pw.println();
                pw.println("SKILLS DEVELOPMENT:");
                pw.println("  Active Training Programs: " + activeTrainings);
                pw.println();
                pw.println("==========================================");
                pw.println("Report Generated: " + java.time.LocalDateTime.now());
                pw.println("==========================================");
            }
            JOptionPane.showMessageDialog(this, 
                "Poverty Reduction Report generated!\n\n" +
                "File: poverty_reduction_report.txt\n\n" +
                "Key Metrics:\n" +
                "• Employment Rate: " + String.format("%.2f%%", employmentRate) + "\n" +
                "• Total Monthly Income: ₱" + String.format("%,.2f", totalIncome) + "\n" +
                "• Average Income: ₱" + String.format("%,.2f", avgIncome),
                "Report Generated", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.replace(",", " ").replace("\n", " ");
    }

    private JButton createButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(color.darker(), 1, true));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(0, 40));
        return b;
    }
}
