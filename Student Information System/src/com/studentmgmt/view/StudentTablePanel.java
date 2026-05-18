package com.studentmgmt.view;

import com.studentmgmt.model.Student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Table panel displaying all student records with search, edit, and delete.
 */
public class StudentTablePanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private final JLabel countLabel;

    private Runnable onEditCallback;
    private Runnable onDeleteCallback;
    private java.util.function.Consumer<String> onSearchCallback;

    private static final String[] COLUMNS = {
        "ID", "Name", "Age", "DOB", "Gender", "Mobile", "Email",
        "Course", "Avg Marks", "GPA", "Grade", "Status"
    };

    public StudentTablePanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(ThemeManager.BG_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(ThemeManager.BG_SURFACE);
        JLabel title = new JLabel("\uD83D\uDCCB  Student Records");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(ThemeManager.BG_SURFACE);
        searchField = ThemeManager.createTextField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name, ID, or course...");
        JButton searchBtn = ThemeManager.createActionButton("\uD83D\uDD0D Search", ThemeManager.ACCENT_BLUE);
        searchBtn.addActionListener(e -> {
            if (onSearchCallback != null) onSearchCallback.accept(searchField.getText());
        });
        searchField.addActionListener(e -> {
            if (onSearchCallback != null) onSearchCallback.accept(searchField.getText());
        });
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        header.add(searchPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(ThemeManager.BG_DARK);
        table.setForeground(ThemeManager.TEXT_PRIMARY);
        table.setSelectionBackground(ThemeManager.TABLE_SELECT);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(ThemeManager.FONT_BODY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Header renderer
        JTableHeader th = table.getTableHeader();
        th.setBackground(ThemeManager.TABLE_HEADER);
        th.setForeground(ThemeManager.TEXT_SECONDARY);
        th.setFont(ThemeManager.FONT_BUTTON);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeManager.ACCENT_BLUE));
        th.setPreferredSize(new Dimension(0, 40));

        // Status column renderer (color-coded) — index 11
        int statusCol = COLUMNS.length - 1;
        table.getColumnModel().getColumn(statusCol).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? ThemeManager.BG_DARK : ThemeManager.TABLE_ROW_ALT);
                    if ("PASS".equals(val)) setForeground(ThemeManager.ACCENT_GREEN);
                    else setForeground(ThemeManager.ACCENT_RED);
                }
                return c;
            }
        });

        // Alternating row renderer for other columns
        DefaultTableCellRenderer altRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? ThemeManager.BG_DARK : ThemeManager.TABLE_ROW_ALT);
                    c.setForeground(ThemeManager.TEXT_PRIMARY);
                }
                // Left-align: Name(1), DOB(3), Gender(4), Mobile(5), Email(6), Course(7)
                setHorizontalAlignment(
                    (col == 1 || col == 3 || col == 4 || col == 5 || col == 6 || col == 7)
                    ? LEFT : CENTER);
                return c;
            }
        };
        for (int i = 0; i < COLUMNS.length - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(altRenderer);
        }

        // Column widths: ID, Name, Age, DOB, Gender, Mobile, Email, Course, AvgMark, GPA, Grade, Status
        int[] widths = {50, 140, 45, 90, 80, 110, 160, 110, 80, 55, 55, 65};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(ThemeManager.BG_DARK);
        scrollPane.getViewport().setBackground(ThemeManager.BG_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(ThemeManager.BG_SURFACE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        btnPanel.setBackground(ThemeManager.BG_SURFACE);
        JButton editBtn   = ThemeManager.createActionButton("\u270F\uFE0F  Edit Selected",   ThemeManager.ACCENT_BLUE);
        JButton deleteBtn = ThemeManager.createActionButton("\uD83D\uDDD1\uFE0F  Delete Selected", ThemeManager.ACCENT_RED);
        editBtn.addActionListener(e   -> { if (onEditCallback   != null) onEditCallback.run(); });
        deleteBtn.addActionListener(e -> { if (onDeleteCallback != null) onDeleteCallback.run(); });
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        countLabel = ThemeManager.createStatusLabel("0 records");
        countLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));

        bottom.add(btnPanel,    BorderLayout.WEST);
        bottom.add(countLabel,  BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    public void refreshTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getName(), s.getAge(),
                s.getDob(), s.getGender(), s.getMobile(), s.getEmail(),
                s.getCourse(),
                String.format("%.1f", s.getAverageMarks()),
                String.format("%.2f", s.getGpa()),
                s.getLetterGrade(), s.getStatus()
            });
        }
        countLabel.setText(students.size() + " record" + (students.size() != 1 ? "s" : ""));
    }

    public int getSelectedStudentId() {
        int row = table.getSelectedRow();
        if (row < 0) return -1;
        return (int) tableModel.getValueAt(row, 0);
    }

    public void setOnEdit(Runnable cb)   { this.onEditCallback   = cb; }
    public void setOnDelete(Runnable cb) { this.onDeleteCallback = cb; }
    public void setOnSearch(java.util.function.Consumer<String> cb) { this.onSearchCallback = cb; }
}
