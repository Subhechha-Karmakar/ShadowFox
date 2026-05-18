package com.library.ui.panels;

import com.library.model.User;
import com.library.service.AuthService;
import com.library.ui.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * 👥 Users Panel (Admin only)
 * - List all users
 * - Create a new user (Admin or Member)
 * - Delete a user
 */
public class UserPanel extends JPanel {

    private final AuthService authService;

    private DefaultTableModel tableModel;
    private JTable            table;

    private static final String[] COLUMNS =
        {"ID", "Username", "Full Name", "Email", "Role", "Created"};

    public UserPanel(AuthService authService) {
        this.authService = authService;

        setLayout(new BorderLayout(0, 0));
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        refreshTable();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.BG_DARK);
        panel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel title = UIUtils.headerLabel("👥 User Management");
        panel.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(UIUtils.BG_DARK);

        JButton addBtn = UIUtils.accentButton("＋ New User");
        addBtn.setName("addUserBtn");
        JButton delBtn = UIUtils.dangerButton("🗑 Delete");
        delBtn.setName("deleteUserBtn");
        JButton refBtn = UIUtils.successButton("⟳ Refresh");
        refBtn.setName("userRefreshBtn");

        addBtn.addActionListener(e -> showCreateUserDialog());
        delBtn.addActionListener(e -> deleteSelectedUser());
        refBtn.addActionListener(e -> refreshTable());

        actions.add(addBtn);
        actions.add(delBtn);
        actions.add(refBtn);
        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIUtils.styleTable(table);
        table.setName("usersTable");
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(4).setMaxWidth(80);
        return UIUtils.darkScrollPane(table);
    }

    // ── Refresh table ─────────────────────────────────────────────────────────
    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<User> users = authService.getUserDAO().findAll();
            for (User u : users) {
                tableModel.addRow(new Object[]{
                    u.getUserId(), u.getUsername(), u.getFullName(),
                    u.getEmail(), u.getRole().name(), u.getCreatedAt()
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Failed to load users: " + ex.getMessage());
        }
    }

    // ── Create user dialog ────────────────────────────────────────────────────
    private void showCreateUserDialog() {
        JTextField usernameField = UIUtils.styledField(18);
        usernameField.setName("newUsernameField");
        JPasswordField passField = UIUtils.styledPasswordField(18);
        passField.setName("newPasswordField");
        JTextField nameField  = UIUtils.styledField(18);
        nameField.setName("newNameField");
        JTextField emailField = UIUtils.styledField(18);
        emailField.setName("newEmailField");

        // Role combo — dark text on light bg so ADMIN / MEMBER are clearly readable
        JComboBox<User.Role> roleCombo = new JComboBox<>(User.Role.values());
        roleCombo.setName("newRoleCombo");
        roleCombo.setFont(UIUtils.FONT_BODY);
        roleCombo.setForeground(new Color(15,  23,  42));    // near-black
        roleCombo.setBackground(new Color(203, 213, 225));   // slate-300 (light)

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBackground(UIUtils.BG_PANEL);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        panel.add(labelFor("Username *:")); panel.add(usernameField);
        panel.add(labelFor("Password *:")); panel.add(passField);
        panel.add(labelFor("Full Name:"));  panel.add(nameField);
        panel.add(labelFor("Email:"));      panel.add(emailField);
        panel.add(labelFor("Role:"));       panel.add(roleCombo);

        // ── Dark-styled OK / Cancel buttons ──────────────────────────────────
        Color darkBg    = new Color(30,  41,  59);   // slate-800
        Color darkHover = new Color(15,  23,  42);   // slate-900

        JButton okBtn     = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        for (JButton btn : new JButton[]{ okBtn, cancelBtn }) {
            btn.setBackground(darkBg);
            btn.setForeground(Color.WHITE);
            btn.setFont(UIUtils.FONT_TITLE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(8, 22, 8, 22));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(darkHover); }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(darkBg);    }
            });
        }

        // Assemble main content with button row at bottom
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(UIUtils.BG_PANEL);
        btnRow.add(cancelBtn);
        btnRow.add(okBtn);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(UIUtils.BG_PANEL);
        content.add(panel,  BorderLayout.CENTER);
        content.add(btnRow, BorderLayout.SOUTH);

        // Build dialog
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this) instanceof Frame
            ? (Frame)  SwingUtilities.getWindowAncestor(this) : null,
            "Create New User", true);
        dlg.setContentPane(content);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(380, dlg.getHeight()));
        dlg.setLocationRelativeTo(this);

        // Track whether OK was clicked
        final boolean[] confirmed = { false };
        okBtn    .addActionListener(e -> { confirmed[0] = true;  dlg.dispose(); });
        cancelBtn.addActionListener(e -> dlg.dispose());

        dlg.setVisible(true);
        if (!confirmed[0]) return;

        try {
            authService.register(
                usernameField.getText().trim(),
                new String(passField.getPassword()),
                nameField.getText().trim(),
                emailField.getText().trim(),
                (User.Role) roleCombo.getSelectedItem()
            );
            refreshTable();
            UIUtils.showInfo(this, "User created successfully.");
        } catch (IllegalArgumentException iae) {
            UIUtils.showError(this, iae.getMessage());
        } catch (SQLException ex) {
            UIUtils.showError(this, "DB error: " + ex.getMessage());
        }
    }

    // ── Delete user ───────────────────────────────────────────────────────────
    private void deleteSelectedUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIUtils.showError(this, "Select a user to delete.");
            return;
        }
        int id       = (int)    tableModel.getValueAt(row, 0);
        String uname = (String) tableModel.getValueAt(row, 1);

        int confirm = UIUtils.showDarkConfirm(this,
            "Delete user: " + uname + "?", "Confirm Delete");
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            authService.deleteUser(id);
            refreshTable();
        } catch (SQLException ex) {
            UIUtils.showError(this, "Cannot delete: " + ex.getMessage());
        }
    }

    private JLabel labelFor(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIUtils.FONT_BODY);
        lbl.setForeground(UIUtils.TEXT_MUTED);
        return lbl;
    }
}
