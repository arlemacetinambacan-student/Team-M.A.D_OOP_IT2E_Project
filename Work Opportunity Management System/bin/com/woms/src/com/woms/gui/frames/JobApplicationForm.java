package com.woms.gui.frames;

// JobApplicationForm.java - Form dialog for job application with personal details
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class JobApplicationForm extends JDialog {
    private JTextField firstNameF, middleNameF, lastNameF;
    private JComboBox<String> genderCombo;
    private JTextField ageF, addressF;
    private JTextArea experienceArea;
    private boolean submitted = false;
    private ApplicationData data;
    public static class ApplicationData {
        public int jobId;
        public String jobTitle;
        public String firstName;
        public String middleName;
        public String lastName;
        public String gender;
        public int age;
        public String address;
        public String experience;
    }

    private String jobTitle;
    private int jobId;

    public JobApplicationForm(JFrame parent, int jobId, String jobTitle) {
        super(parent, "Job Application Form", true);
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        setSize(650, 750);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(248, 250, 252));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        // Header with Job Information
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel header = new JLabel("Job Application Form");
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(new Color(40, 80, 200));
        headerPanel.add(header, BorderLayout.NORTH);
        
        // Job Selection Display
        JPanel jobInfoPanel = new JPanel(new BorderLayout());
        jobInfoPanel.setBackground(new Color(230, 240, 255));
        jobInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(60, 130, 255), 2, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        JLabel jobLabel = new JLabel("Applying for:");
        jobLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jobLabel.setForeground(new Color(100, 100, 100));
        jobInfoPanel.add(jobLabel, BorderLayout.NORTH);
        
        JLabel jobTitleLabel = new JLabel(jobTitle);
        jobTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jobTitleLabel.setForeground(new Color(40, 80, 200));
        jobInfoPanel.add(jobTitleLabel, BorderLayout.CENTER);
        
        headerPanel.add(jobInfoPanel, BorderLayout.SOUTH);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        root.add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        // First Name
        gbc.gridy = 0;
        form.add(label("First Name *"), gbc);
        gbc.gridy = 1;
        firstNameF = new JTextField();
        firstNameF.setPreferredSize(new Dimension(0, 35));
        firstNameF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(firstNameF);
        form.add(firstNameF, gbc);

        // Middle Name
        gbc.gridy = 2;
        form.add(label("Middle Name"), gbc);
        gbc.gridy = 3;
        middleNameF = new JTextField();
        middleNameF.setPreferredSize(new Dimension(0, 35));
        middleNameF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(middleNameF);
        form.add(middleNameF, gbc);

        // Last Name
        gbc.gridy = 4;
        form.add(label("Last Name *"), gbc);
        gbc.gridy = 5;
        lastNameF = new JTextField();
        lastNameF.setPreferredSize(new Dimension(0, 35));
        lastNameF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(lastNameF);
        form.add(lastNameF, gbc);

        // Gender
        gbc.gridy = 6;
        form.add(label("Gender *"), gbc);
        gbc.gridy = 7;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other", "Prefer not to say"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(genderCombo, gbc);

        // Age
        gbc.gridy = 8;
        form.add(label("Age *"), gbc);
        gbc.gridy = 9;
        ageF = new JTextField();
        ageF.setPreferredSize(new Dimension(0, 35));
        ageF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(ageF);
        form.add(ageF, gbc);

        // Address
        gbc.gridy = 10;
        form.add(label("Address *"), gbc);
        gbc.gridy = 11;
        addressF = new JTextField();
        addressF.setPreferredSize(new Dimension(0, 35));
        addressF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        styleField(addressF);
        form.add(addressF, gbc);

        // Experience
        gbc.gridy = 12;
        form.add(label("Work Experience *"), gbc);
        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        experienceArea = new JTextArea(5, 30);
        experienceArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        experienceArea.setLineWrap(true);
        experienceArea.setWrapStyleWord(true);
        experienceArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        form.add(new JScrollPane(experienceArea), gbc);

        // Buttons
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);

        JButton submitBtn = new JButton("Submit Application");
        submitBtn.setBackground(new Color(60, 180, 80));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> submit());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(150, 150, 150));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);
        form.add(btnPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        root.add(scrollPane, BorderLayout.CENTER);
    }

    private void submit() {
        String firstName = firstNameF.getText().trim();
        String middleName = middleNameF.getText().trim();
        String lastName = lastNameF.getText().trim();
        String gender = (String) genderCombo.getSelectedItem();
        String ageStr = ageF.getText().trim();
        String address = addressF.getText().trim();
        String experience = experienceArea.getText().trim();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name and Last Name are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Age is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 18 || age > 100) {
                JOptionPane.showMessageDialog(this, "Age must be between 18 and 100.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (experience.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Work Experience is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        data = new ApplicationData();
        data.jobId = this.jobId;
        data.jobTitle = this.jobTitle;
        data.firstName = firstName;
        data.middleName = middleName;
        data.lastName = lastName;
        data.gender = gender;
        data.age = age;
        data.address = address;
        data.experience = experience;

        submitted = true;
        dispose();
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public ApplicationData getData() {
        return data;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(60, 60, 60));
        return l;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }
}

