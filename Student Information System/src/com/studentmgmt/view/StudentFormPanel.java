package com.studentmgmt.view;

import com.studentmgmt.model.Student;
import com.studentmgmt.util.ValidationUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Form panel for adding / editing student records.
 */
public class StudentFormPanel extends JPanel {

    private final JTextField idField;
    private final JTextField nameField;
    private final JTextField ageField;
    private final JComboBox<String> courseBox;
    private final JTextField[] markFields;
    private final JLabel gpaValueLabel;
    private final JLabel statusValueLabel;
    private final JLabel gradeValueLabel;
    private final JButton addButton;
    private final JButton updateButton;
    private final JButton clearButton;

    // New personal detail fields
    private final JTextField dobField;
    private final JComboBox<String> genderBox;
    private final JTextField mobileField;
    private final JTextField emailField;

    private Runnable onAddCallback;
    private Runnable onUpdateCallback;

    public StudentFormPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ThemeManager.BG_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("\uD83C\uDF93  Add / Edit Student");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel formBody = new JPanel();
        formBody.setLayout(new BoxLayout(formBody, BoxLayout.Y_AXIS));
        formBody.setBackground(ThemeManager.BG_SURFACE);

        // ── Student Basic Info card ──────────────────────────────────
        JPanel infoCard = ThemeManager.createCardPanel();
        infoCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Student ID | Full Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        infoCard.add(ThemeManager.createFormLabel("Student ID *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        idField = ThemeManager.createTextField(10);
        ValidationUtils.applyNumericFilter(idField);
        infoCard.add(idField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        infoCard.add(ThemeManager.createFormLabel("Full Name *"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        nameField = ThemeManager.createTextField(15);
        infoCard.add(nameField, gbc);

        // Row 1: Age | Course
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        infoCard.add(ThemeManager.createFormLabel("Age *"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        ageField = ThemeManager.createTextField(5);
        ValidationUtils.applyNumericFilter(ageField);
        infoCard.add(ageField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        infoCard.add(ThemeManager.createFormLabel("Course *"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        courseBox = ThemeManager.createComboBox(Student.COURSES);
        infoCard.add(courseBox, gbc);

        formBody.add(infoCard);
        formBody.add(Box.createVerticalStrut(12));

        // ── Personal Details card ────────────────────────────────────
        JPanel personalCard = ThemeManager.createCardPanel();
        personalCard.setLayout(new GridBagLayout());
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(6, 8, 6, 8);
        pgbc.fill = GridBagConstraints.HORIZONTAL;

        // Section title
        pgbc.gridx = 0; pgbc.gridy = 0; pgbc.gridwidth = 4; pgbc.weightx = 1;
        personalCard.add(ThemeManager.createSectionTitle("Personal Details"), pgbc);
        pgbc.gridwidth = 1;

        // Row 1: DOB | Gender
        pgbc.gridx = 0; pgbc.gridy = 1; pgbc.weightx = 0;
        personalCard.add(ThemeManager.createFormLabel("DOB (DD/MM/YYYY)"), pgbc);
        pgbc.gridx = 1; pgbc.weightx = 1;
        dobField = ThemeManager.createTextField(12);
        dobField.setToolTipText("Enter date of birth in DD/MM/YYYY format");
        personalCard.add(dobField, pgbc);

        pgbc.gridx = 2; pgbc.weightx = 0;
        personalCard.add(ThemeManager.createFormLabel("Gender"), pgbc);
        pgbc.gridx = 3; pgbc.weightx = 1;
        genderBox = ThemeManager.createComboBox(buildGenderOptions());
        personalCard.add(genderBox, pgbc);

        // Row 2: Mobile | Email
        pgbc.gridx = 0; pgbc.gridy = 2; pgbc.weightx = 0;
        personalCard.add(ThemeManager.createFormLabel("Mobile"), pgbc);
        pgbc.gridx = 1; pgbc.weightx = 1;
        mobileField = ThemeManager.createTextField(12);
        mobileField.setToolTipText("e.g. +91 9876543210");
        ValidationUtils.applyPhoneFilter(mobileField);
        personalCard.add(mobileField, pgbc);

        pgbc.gridx = 2; pgbc.weightx = 0;
        personalCard.add(ThemeManager.createFormLabel("Email"), pgbc);
        pgbc.gridx = 3; pgbc.weightx = 1;
        emailField = ThemeManager.createTextField(18);
        emailField.setToolTipText("e.g. student@example.com");
        personalCard.add(emailField, pgbc);

        formBody.add(personalCard);
        formBody.add(Box.createVerticalStrut(12));

        // ── Marks card ───────────────────────────────────────────────
        JPanel marksCard = ThemeManager.createCardPanel();
        marksCard.setLayout(new GridBagLayout());
        GridBagConstraints mgbc = new GridBagConstraints();
        mgbc.insets = new Insets(6, 8, 6, 8);
        mgbc.fill = GridBagConstraints.HORIZONTAL;

        mgbc.gridx = 0; mgbc.gridy = 0; mgbc.gridwidth = 4; mgbc.weightx = 1;
        marksCard.add(ThemeManager.createSectionTitle("Subject Marks (0\u2013100)"), mgbc);
        mgbc.gridwidth = 1;

        markFields = new JTextField[Student.SUBJECTS.length];
        DocumentListener gpaUpdater = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateGpaPreview(); }
            public void removeUpdate(DocumentEvent e) { updateGpaPreview(); }
            public void changedUpdate(DocumentEvent e) { updateGpaPreview(); }
        };

        for (int i = 0; i < Student.SUBJECTS.length; i++) {
            int row = 1 + i / 2;
            int colOffset = (i % 2) * 2;
            mgbc.gridx = colOffset; mgbc.gridy = row; mgbc.weightx = 0;
            marksCard.add(ThemeManager.createFormLabel(Student.SUBJECTS[i]), mgbc);
            mgbc.gridx = colOffset + 1; mgbc.weightx = 1;
            markFields[i] = ThemeManager.createTextField(6);
            ValidationUtils.applyDecimalFilter(markFields[i]);
            markFields[i].getDocument().addDocumentListener(gpaUpdater);
            marksCard.add(markFields[i], mgbc);
        }

        formBody.add(marksCard);
        formBody.add(Box.createVerticalStrut(12));

        // ── GPA preview card ─────────────────────────────────────────
        JPanel gpaCard = ThemeManager.createCardPanel();
        gpaCard.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 8));

        gpaCard.add(ThemeManager.createFormLabel("GPA:"));
        gpaValueLabel = new JLabel("\u2014");
        gpaValueLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        gpaValueLabel.setForeground(ThemeManager.ACCENT_BLUE);
        gpaCard.add(gpaValueLabel);

        gpaCard.add(ThemeManager.createFormLabel("Grade:"));
        gradeValueLabel = new JLabel("\u2014");
        gradeValueLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        gradeValueLabel.setForeground(ThemeManager.ACCENT_PURPLE);
        gpaCard.add(gradeValueLabel);

        gpaCard.add(ThemeManager.createFormLabel("Status:"));
        statusValueLabel = new JLabel("\u2014");
        statusValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusValueLabel.setForeground(ThemeManager.TEXT_MUTED);
        gpaCard.add(statusValueLabel);

        formBody.add(gpaCard);
        formBody.add(Box.createVerticalStrut(16));

        // ── Action buttons ───────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnPanel.setBackground(ThemeManager.BG_SURFACE);

        addButton    = ThemeManager.createActionButton("\u2795  Add Student", new Color(35, 134, 54));
        updateButton = ThemeManager.createActionButton("\u270F\uFE0F  Update", ThemeManager.ACCENT_BLUE);
        clearButton  = ThemeManager.createActionButton("Clear", ThemeManager.BG_ELEVATED);
        clearButton.setForeground(ThemeManager.TEXT_SECONDARY);

        addButton.addActionListener(e    -> { if (onAddCallback    != null) onAddCallback.run(); });
        updateButton.addActionListener(e -> { if (onUpdateCallback != null) onUpdateCallback.run(); });
        clearButton.addActionListener(e  -> clearForm());

        btnPanel.add(addButton);
        btnPanel.add(updateButton);
        btnPanel.add(clearButton);
        formBody.add(btnPanel);

        JScrollPane scroll = new JScrollPane(formBody);
        scroll.setBackground(ThemeManager.BG_SURFACE);
        scroll.getViewport().setBackground(ThemeManager.BG_SURFACE);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    /** Build gender options array with a blank leading entry. */
    private String[] buildGenderOptions() {
        String[] opts = new String[Student.GENDERS.length + 1];
        opts[0] = "";
        System.arraycopy(Student.GENDERS, 0, opts, 1, Student.GENDERS.length);
        return opts;
    }

    private void updateGpaPreview() {
        double[] marks = new double[markFields.length];
        for (int i = 0; i < markFields.length; i++) {
            String txt = markFields[i].getText().trim();
            if (txt.isEmpty() || !ValidationUtils.isValidMark(txt)) {
                gpaValueLabel.setText("\u2014"); gradeValueLabel.setText("\u2014");
                statusValueLabel.setText("\u2014"); statusValueLabel.setForeground(ThemeManager.TEXT_MUTED);
                return;
            }
            marks[i] = Double.parseDouble(txt);
        }
        Student temp = new Student(0, "tmp", 18, "tmp", marks);
        gpaValueLabel.setText(String.format("%.2f / 4.00", temp.getGpa()));
        gradeValueLabel.setText(temp.getLetterGrade());
        statusValueLabel.setText(temp.isPassing() ? "\u2713 PASS" : "\u2717 FAIL");
        statusValueLabel.setForeground(temp.isPassing() ? ThemeManager.ACCENT_GREEN : ThemeManager.ACCENT_RED);
    }

    public void setOnAdd(Runnable cb)    { this.onAddCallback    = cb; }
    public void setOnUpdate(Runnable cb) { this.onUpdateCallback = cb; }

    public String validateInput() {
        if (!ValidationUtils.isValidId(idField.getText()))     return "Student ID must be a positive number.";
        if (!ValidationUtils.isValidName(nameField.getText())) return "Name cannot be empty.";
        if (!ValidationUtils.isValidAge(ageField.getText()))   return "Age must be between 1 and 150.";
        if (!ValidationUtils.isValidDob(dobField.getText()))   return "DOB must be in DD/MM/YYYY format.";
        if (!ValidationUtils.isValidMobile(mobileField.getText()))
            return "Mobile number appears invalid (7\u201315 digits).";
        if (!ValidationUtils.isValidEmail(emailField.getText()))
            return "Email address format is invalid.";
        for (int i = 0; i < markFields.length; i++) {
            if (!ValidationUtils.isValidMark(markFields[i].getText()))
                return Student.SUBJECTS[i] + " mark must be 0\u2013100.";
        }
        return null;
    }

    public Student buildStudent() {
        return new Student(
            Integer.parseInt(idField.getText().trim()),
            nameField.getText().trim(),
            Integer.parseInt(ageField.getText().trim()),
            (String) courseBox.getSelectedItem(),
            getMarksArray(),
            dobField.getText().trim(),
            (String) genderBox.getSelectedItem(),
            mobileField.getText().trim(),
            emailField.getText().trim());
    }

    private double[] getMarksArray() {
        double[] m = new double[markFields.length];
        for (int i = 0; i < m.length; i++) m[i] = Double.parseDouble(markFields[i].getText().trim());
        return m;
    }

    public void populateFrom(Student s) {
        idField.setText(String.valueOf(s.getId()));
        idField.setEditable(false);
        nameField.setText(s.getName());
        ageField.setText(String.valueOf(s.getAge()));
        courseBox.setSelectedItem(s.getCourse());
        dobField.setText(s.getDob());
        genderBox.setSelectedItem(s.getGender().isEmpty() ? "" : s.getGender());
        mobileField.setText(s.getMobile());
        emailField.setText(s.getEmail());
        double[] marks = s.getMarks();
        for (int i = 0; i < markFields.length; i++) markFields[i].setText(String.format("%.0f", marks[i]));
    }

    public void clearForm() {
        idField.setText(""); idField.setEditable(true);
        nameField.setText(""); ageField.setText("");
        courseBox.setSelectedIndex(0);
        dobField.setText(""); genderBox.setSelectedIndex(0);
        mobileField.setText(""); emailField.setText("");
        for (JTextField mf : markFields) mf.setText("");
        gpaValueLabel.setText("\u2014"); gradeValueLabel.setText("\u2014");
        statusValueLabel.setText("\u2014"); statusValueLabel.setForeground(ThemeManager.TEXT_MUTED);
    }

    public int getStudentId() { return Integer.parseInt(idField.getText().trim()); }
}
