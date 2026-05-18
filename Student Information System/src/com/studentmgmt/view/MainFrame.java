package com.studentmgmt.view;

import com.studentmgmt.controller.StudentController;
import com.studentmgmt.model.Student;
import com.studentmgmt.util.PdfExporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Main application frame — wires MVC together.
 * Sidebar navigation + CardLayout content switching + status bar.
 */
public class MainFrame extends JFrame {

    private final StudentController controller = new StudentController();

    private final StudentFormPanel formPanel;
    private final StudentTablePanel tablePanel;
    private final PieChartPanel chartPanel;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    // Nav buttons
    private final JButton navAdd;
    private final JButton navView;
    private final JButton navChart;
    private final JButton navExport;
    private JButton activeNav;

    // Status bar labels
    private final JLabel statusTotal;
    private final JLabel statusPass;
    private final JLabel statusFail;
    private final JLabel statusGpa;

    public MainFrame() {
        super("Student Information System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 680));
        setPreferredSize(new Dimension(1100, 750));

        // Root
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeManager.BG_DARK);
        setContentPane(root);

        // ── Header ──────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(13, 17, 23));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));
        JLabel appTitle = new JLabel("\uD83C\uDF93  Student Information System");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        header.add(appTitle, BorderLayout.WEST);

        JLabel version = new JLabel("v1.0  \u2022  MVC Architecture");
        version.setFont(ThemeManager.FONT_SMALL);
        version.setForeground(ThemeManager.TEXT_MUTED);
        header.add(version, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Sidebar ─────────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeManager.BG_SURFACE);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.BORDER_COLOR));

        sidebar.add(Box.createVerticalStrut(16));
        JLabel menuLabel = ThemeManager.createStatusLabel("   MENU");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuLabel.setAlignmentX(LEFT_ALIGNMENT);
        sidebar.add(menuLabel);
        sidebar.add(Box.createVerticalStrut(8));

        navAdd    = ThemeManager.createNavButton("Add Student", "\u2795");
        navView   = ThemeManager.createNavButton("View Records", "\uD83D\uDCCB");
        navChart  = ThemeManager.createNavButton("Analytics", "\uD83D\uDCCA");
        navExport = ThemeManager.createNavButton("Export PDF", "\uD83D\uDCC4");

        navAdd.addActionListener(e -> switchCard("form", navAdd));
        navView.addActionListener(e -> { refreshTable(); switchCard("table", navView); });
        navChart.addActionListener(e -> { refreshChart(); switchCard("chart", navChart); });
        navExport.addActionListener(e -> exportPdf());

        sidebar.add(navAdd);
        sidebar.add(navView);
        sidebar.add(navChart);
        sidebar.add(navExport);
        sidebar.add(Box.createVerticalGlue());
        root.add(sidebar, BorderLayout.WEST);

        // ── Content area (CardLayout) ───────────────────────────────
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ThemeManager.BG_SURFACE);

        formPanel = new StudentFormPanel();
        tablePanel = new StudentTablePanel();
        chartPanel = new PieChartPanel();

        contentPanel.add(formPanel, "form");
        contentPanel.add(tablePanel, "table");
        contentPanel.add(chartPanel, "chart");

        root.add(contentPanel, BorderLayout.CENTER);

        // ── Status bar ──────────────────────────────────────────────
        JPanel statusBar = ThemeManager.createStatusBar();
        statusTotal = ThemeManager.createStatusLabel("Total: 0");
        statusPass  = ThemeManager.createStatusLabel("Pass: 0");
        statusFail  = ThemeManager.createStatusLabel("Fail: 0");
        statusGpa   = ThemeManager.createStatusLabel("Avg GPA: 0.00");
        statusPass.setForeground(ThemeManager.ACCENT_GREEN);
        statusFail.setForeground(ThemeManager.ACCENT_RED);
        statusGpa.setForeground(ThemeManager.ACCENT_BLUE);
        statusBar.add(statusTotal);
        statusBar.add(statusPass);
        statusBar.add(statusFail);
        statusBar.add(statusGpa);
        root.add(statusBar, BorderLayout.SOUTH);

        // ── Wire callbacks ──────────────────────────────────────────
        formPanel.setOnAdd(this::handleAdd);
        formPanel.setOnUpdate(this::handleUpdate);
        tablePanel.setOnEdit(this::handleEdit);
        tablePanel.setOnDelete(this::handleDelete);
        tablePanel.setOnSearch(this::handleSearch);

        // Start on Add tab
        switchCard("form", navAdd);
        pack();
        setLocationRelativeTo(null);
    }

    // ── Navigation ──────────────────────────────────────────────────

    private void switchCard(String card, JButton nav) {
        cardLayout.show(contentPanel, card);
        if (activeNav != null) ThemeManager.setNavActive(activeNav, false);
        ThemeManager.setNavActive(nav, true);
        activeNav = nav;
    }

    // ── CRUD Handlers ───────────────────────────────────────────────

    private void handleAdd() {
        String err = formPanel.validateInput();
        if (err != null) { showError(err); return; }
        Student s = formPanel.buildStudent();
        if (!controller.addStudent(s)) {
            showError("A student with ID " + s.getId() + " already exists.");
            return;
        }
        formPanel.clearForm();
        refreshStatus();
        showSuccess("Student added successfully!");
    }

    private void handleUpdate() {
        String err = formPanel.validateInput();
        if (err != null) { showError(err); return; }
        Student s = formPanel.buildStudent();
        if (!controller.updateStudent(s.getId(), s.getName(), s.getAge(),
                s.getCourse(), s.getMarks())) {
            showError("Student with ID " + s.getId() + " not found.");
            return;
        }
        formPanel.clearForm();
        refreshStatus();
        showSuccess("Student updated successfully!");
    }

    private void handleEdit() {
        int id = tablePanel.getSelectedStudentId();
        if (id < 0) { showError("Please select a student row first."); return; }
        Student s = controller.getStudentById(id);
        if (s == null) return;
        formPanel.populateFrom(s);
        switchCard("form", navAdd);
    }

    private void handleDelete() {
        int id = tablePanel.getSelectedStudentId();
        if (id < 0) { showError("Please select a student row first."); return; }

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student ID " + id + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            controller.deleteStudent(id);
            refreshTable();
            refreshStatus();
            showSuccess("Student deleted.");
        }
    }

    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            tablePanel.refreshTable(controller.getAllStudents());
        } else {
            tablePanel.refreshTable(controller.searchStudents(query));
        }
    }

    // ── Refresh helpers ─────────────────────────────────────────────

    private void refreshTable() {
        tablePanel.refreshTable(controller.getAllStudents());
    }

    private void refreshChart() {
        chartPanel.updateData(controller.getPassCount(), controller.getFailCount());
    }

    private void refreshStatus() {
        statusTotal.setText("Total: " + controller.getTotalStudents());
        statusPass.setText("Pass: " + controller.getPassCount());
        statusFail.setText("Fail: " + controller.getFailCount());
        statusGpa.setText("Avg GPA: " + String.format("%.2f", controller.getAverageGpa()));
    }

    // ── PDF Export ───────────────────────────────────────────────────

    private void exportPdf() {
        if (controller.getTotalStudents() == 0) {
            showError("No students to export. Add some records first.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Export Student Report as PDF");
        fc.setSelectedFile(new File("Student_Report.pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                PdfExporter.export(controller.getAllStudents(), fc.getSelectedFile());
                showSuccess("PDF exported to:\n" + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                showError("PDF export failed: " + ex.getMessage());
            }
        }
    }

    // ── Dialogs ─────────────────────────────────────────────────────

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
