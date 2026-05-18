package com.studentmgmt.view;

import com.studentmgmt.controller.StudentController;
import com.studentmgmt.model.Student;
import com.studentmgmt.util.PdfExporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Main application window — houses the sidebar, content area and status bar.
 * Follows MVC: this is the View; event handling delegates to StudentController.
 *
 * Layout:
 *   WEST  – Sidebar (nav buttons + stats)
 *   CENTER – CardLayout content (Form, Table, Chart)
 *   SOUTH  – Status bar
 */
public class StudentView extends JFrame {

    private final StudentController controller;

    // ── Panels ──────────────────────────────────────────────────────
    private final StudentFormPanel  formPanel;
    private final StudentTablePanel tablePanel;

    // ── Sidebar nav buttons ─────────────────────────────────────────
    private JButton navAdd;
    private JButton navRecords;
    private JButton navChart;
    private JButton navExport;

    // ── Stats labels ─────────────────────────────────────────────────
    private JLabel statTotal;
    private JLabel statPass;
    private JLabel statFail;
    private JLabel statGpa;

    // ── Status bar ───────────────────────────────────────────────────
    private JLabel statusLabel;

    // ── CardLayout for content area ──────────────────────────────────
    private final JPanel   contentArea;
    private final CardLayout cardLayout;
    private static final String CARD_FORM  = "FORM";
    private static final String CARD_TABLE = "TABLE";
    private static final String CARD_CHART = "CHART";

    // ── Chart placeholder (created lazily) ───────────────────────────
    private JPanel chartCardPanel;

    public StudentView() {
        this.controller = new StudentController();
        this.cardLayout = new CardLayout();
        this.contentArea = new JPanel(cardLayout);
        this.formPanel   = new StudentFormPanel();
        this.tablePanel  = new StudentTablePanel();

        initFrame();
        buildSidebar();
        buildContentArea();
        buildStatusBar();
        wireEvents();

        refreshAll();
        showCard(CARD_FORM, navAdd);
        setVisible(true);
    }

    // ────────────────────────────────────────────────────────────────
    //  Frame setup
    // ────────────────────────────────────────────────────────────────

    private void initFrame() {
        setTitle("Student Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 680));
        setPreferredSize(new Dimension(1300, 800));
        setLayout(new BorderLayout());
        getContentPane().setBackground(ThemeManager.BG_DARK);
        setLocationRelativeTo(null);
        pack();
    }

    // ────────────────────────────────────────────────────────────────
    //  Sidebar
    // ────────────────────────────────────────────────────────────────

    private void buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeManager.BG_SURFACE);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.BORDER_COLOR));

        // Logo / app name
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(ThemeManager.BG_SURFACE);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));
        JLabel logo = new JLabel("<html><span style='font-size:18px'>🎓</span>&nbsp;SIS</html>");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(ThemeManager.ACCENT_BLUE);
        JLabel sub = new JLabel("Student Info System");
        sub.setFont(ThemeManager.FONT_SMALL);
        sub.setForeground(ThemeManager.TEXT_MUTED);
        logoPanel.add(logo, BorderLayout.CENTER);
        logoPanel.add(sub, BorderLayout.SOUTH);
        sidebar.add(logoPanel);

        sidebar.add(makeSeparator());

        // Nav buttons
        navAdd     = ThemeManager.createNavButton("Add / Edit",   "➕");
        navRecords = ThemeManager.createNavButton("Records",      "📋");
        navChart   = ThemeManager.createNavButton("Analytics",    "📊");
        navExport  = ThemeManager.createNavButton("Export PDF",   "📄");

        for (JButton btn : new JButton[]{navAdd, navRecords, navChart, navExport}) {
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalStrut(24));
        sidebar.add(makeSeparator());

        // Stats section
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(ThemeManager.BG_SURFACE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel statsTitle = ThemeManager.createFormLabel("QUICK STATS");
        statsTitle.setForeground(ThemeManager.TEXT_MUTED);
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(statsTitle);
        statsPanel.add(Box.createVerticalStrut(12));

        statTotal = makeStatLabel("Total Students", "0", ThemeManager.ACCENT_BLUE);
        statPass  = makeStatLabel("Passing",  "0", ThemeManager.ACCENT_GREEN);
        statFail  = makeStatLabel("Failing",  "0", ThemeManager.ACCENT_RED);
        statGpa   = makeStatLabel("Avg GPA",  "0.00", ThemeManager.ACCENT_PURPLE);

        for (JPanel p : new JPanel[]{
                wrapStatRow(statTotal), wrapStatRow(statPass),
                wrapStatRow(statFail), wrapStatRow(statGpa)}) {
            p.setAlignmentX(Component.LEFT_ALIGNMENT);
            statsPanel.add(p);
            statsPanel.add(Box.createVerticalStrut(8));
        }

        sidebar.add(statsPanel);
        sidebar.add(Box.createVerticalGlue());

        // Version tag
        JLabel version = ThemeManager.createStatusLabel("v1.0.0 · Java " + System.getProperty("java.version").split("\\.")[0]);
        version.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 0));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(version);

        add(sidebar, BorderLayout.WEST);
    }

    private JLabel makeStatLabel(String caption, String value, Color color) {
        JLabel lbl = new JLabel(value);
        lbl.setFont(new Font("Consolas", Font.BOLD, 20));
        lbl.setForeground(color);
        lbl.putClientProperty("caption", caption);
        return lbl;
    }

    private JPanel wrapStatRow(JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(ThemeManager.BG_SURFACE);
        JLabel cap = ThemeManager.createFormLabel((String) valueLabel.getClientProperty("caption") + ":");
        row.add(cap, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.BORDER_COLOR);
        sep.setBackground(ThemeManager.BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ────────────────────────────────────────────────────────────────
    //  Content area
    // ────────────────────────────────────────────────────────────────

    private void buildContentArea() {
        contentArea.setBackground(ThemeManager.BG_DARK);
        contentArea.add(formPanel,  CARD_FORM);
        contentArea.add(tablePanel, CARD_TABLE);

        // Chart placeholder — actual chart built lazily on first open
        chartCardPanel = new JPanel(new BorderLayout());
        chartCardPanel.setBackground(ThemeManager.BG_DARK);
        JLabel placeholder = new JLabel("Loading chart…", SwingConstants.CENTER);
        placeholder.setForeground(ThemeManager.TEXT_MUTED);
        chartCardPanel.add(placeholder, BorderLayout.CENTER);
        contentArea.add(chartCardPanel, CARD_CHART);

        add(contentArea, BorderLayout.CENTER);
    }

    // ────────────────────────────────────────────────────────────────
    //  Status bar
    // ────────────────────────────────────────────────────────────────

    private void buildStatusBar() {
        JPanel bar = ThemeManager.createStatusBar();
        statusLabel = ThemeManager.createStatusLabel("Ready.");
        bar.add(statusLabel);
        add(bar, BorderLayout.SOUTH);
    }

    // ────────────────────────────────────────────────────────────────
    //  Event wiring (MVC Controller logic)
    // ────────────────────────────────────────────────────────────────

    private void wireEvents() {

        // ── Sidebar navigation ───────────────────────────────────────
        navAdd.addActionListener(e -> showCard(CARD_FORM, navAdd));
        navRecords.addActionListener(e -> {
            refreshTable(controller.getAllStudents());
            showCard(CARD_TABLE, navRecords);
        });
        navChart.addActionListener(e -> {
            openChartCard();
            showCard(CARD_CHART, navChart);
        });
        navExport.addActionListener(e -> exportPdf());

        // ── Form: Add ────────────────────────────────────────────────
        formPanel.setOnAdd(() -> {
            String err = formPanel.validateInput();
            if (err != null) { showError(err); return; }
            Student s = formPanel.buildStudent();
            if (!controller.addStudent(s)) {
                showError("Student ID " + s.getId() + " already exists.");
                return;
            }
            formPanel.clearForm();
            refreshAll();
            setStatus("✔  Student \"" + s.getName() + "\" added (ID " + s.getId() + ").");
            showInfo("Student added successfully!");
        });

        // ── Form: Update ─────────────────────────────────────────────
        formPanel.setOnUpdate(() -> {
            String err = formPanel.validateInput();
            if (err != null) { showError(err); return; }
            Student s = formPanel.buildStudent();
            if (!controller.updateStudent(s.getId(), s.getName(), s.getAge(), s.getCourse(), s.getMarks(),
                    s.getDob(), s.getGender(), s.getMobile(), s.getEmail())) {
                showError("Student ID " + s.getId() + " not found.");
                return;
            }
            formPanel.clearForm();
            refreshAll();
            setStatus("✔  Student ID " + s.getId() + " updated.");
            showInfo("Student updated successfully!");
        });

        // ── Table: Edit selected ─────────────────────────────────────
        tablePanel.setOnEdit(() -> {
            int id = tablePanel.getSelectedStudentId();
            if (id < 0) { showError("Please select a student row to edit."); return; }
            Student s = controller.getStudentById(id);
            if (s == null) return;
            formPanel.populateFrom(s);
            showCard(CARD_FORM, navAdd);
            setStatus("Editing student ID " + id + " — " + s.getName());
        });

        // ── Table: Delete selected ───────────────────────────────────
        tablePanel.setOnDelete(() -> {
            int id = tablePanel.getSelectedStudentId();
            if (id < 0) { showError("Please select a student row to delete."); return; }
            Student s = controller.getStudentById(id);
            if (s == null) return;

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "<html>Are you sure you want to delete:<br>" +
                    "<b>" + s.getName() + "</b> (ID: " + id + ")?</html>",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                controller.deleteStudent(id);
                refreshAll();
                setStatus("🗑  Student ID " + id + " deleted.");
            }
        });

        // ── Table: Search ────────────────────────────────────────────
        tablePanel.setOnSearch(query -> {
            List<Student> results = controller.searchStudents(query);
            tablePanel.refreshTable(results);
            setStatus("🔍  " + results.size() + " result(s) for \"" + query + "\".");
        });
    }

    // ────────────────────────────────────────────────────────────────
    //  Chart card (lazy-built with JavaFX PieChart)
    // ────────────────────────────────────────────────────────────────

    private boolean chartBuilt = false;

    private void openChartCard() {
        if (!chartBuilt) {
            chartBuilt = true;
            chartCardPanel.removeAll();
            ChartPanel cp = new ChartPanel(
                    controller.getPassCount(),
                    controller.getFailCount(),
                    controller.getTotalStudents(),
                    controller.getAverageGpa());
            chartCardPanel.add(cp, BorderLayout.CENTER);
            chartCardPanel.revalidate();
        } else {
            // rebuild so the chart is always fresh
            chartCardPanel.removeAll();
            ChartPanel cp = new ChartPanel(
                    controller.getPassCount(),
                    controller.getFailCount(),
                    controller.getTotalStudents(),
                    controller.getAverageGpa());
            chartCardPanel.add(cp, BorderLayout.CENTER);
            chartCardPanel.revalidate();
        }
    }

    // ────────────────────────────────────────────────────────────────
    //  PDF Export
    // ────────────────────────────────────────────────────────────────

    private void exportPdf() {
        if (controller.getTotalStudents() == 0) {
            showError("No students to export.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save PDF Report");
        chooser.setSelectedFile(new File("StudentReport.pdf"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf"))
            file = new File(file.getAbsolutePath() + ".pdf");

        try {
            PdfExporter.export(controller.getAllStudents(), file);
            setStatus("📄  Report saved: " + file.getAbsolutePath());
            int open = JOptionPane.showConfirmDialog(this,
                    "PDF saved!\n" + file.getAbsolutePath() + "\n\nOpen file now?",
                    "Export Successful", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (open == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ex) {
            showError("PDF export failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ────────────────────────────────────────────────────────────────
    //  Helpers
    // ────────────────────────────────────────────────────────────────

    private JButton activeNavButton = null;

    private void showCard(String card, JButton nav) {
        cardLayout.show(contentArea, card);
        if (activeNavButton != null) ThemeManager.setNavActive(activeNavButton, false);
        ThemeManager.setNavActive(nav, true);
        activeNavButton = nav;
    }

    private void refreshAll() {
        List<Student> all = controller.getAllStudents();
        tablePanel.refreshTable(all);
        statTotal.setText(String.valueOf(controller.getTotalStudents()));
        statPass.setText(String.valueOf(controller.getPassCount()));
        statFail.setText(String.valueOf(controller.getFailCount()));
        statGpa.setText(String.format("%.2f", controller.getAverageGpa()));
    }

    private void refreshTable(List<Student> students) {
        tablePanel.refreshTable(students);
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
