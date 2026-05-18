package com.library.ui;

import com.library.model.User;
import com.library.service.AuthService;
import com.library.ui.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Modal login dialog.
 * Returns the authenticated User via getAuthenticatedUser().
 * If the user closes the dialog without logging in, returns null (app exits).
 */
public class LoginDialog extends JDialog {

    private final AuthService authService;
    private User authenticatedUser;

    // ── UI Components ─────────────────────────────────────────────────────────
    private final JTextField     usernameField;
    private final JPasswordField passwordField;
    private final JLabel         statusLabel;

    public LoginDialog(Frame parent, AuthService authService) {
        super(parent, "Library System — Sign In", true);
        this.authService = authService;

        // ── Root panel ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.BG_PANEL);
        root.setBorder(new EmptyBorder(40, 50, 40, 50));

        // ── Logo / Title ─────────────────────────────────────────────────────
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIUtils.BG_PANEL);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel("📚", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(UIUtils.FONT_HEADING);
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(UIUtils.FONT_BODY);
        subtitleLabel.setForeground(UIUtils.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(iconLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);
        titlePanel.add(Box.createVerticalStrut(28));
        root.add(titlePanel, BorderLayout.NORTH);

        // ── Form ─────────────────────────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.BG_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        usernameField = UIUtils.styledField(20);
        usernameField.setName("usernameField");
        passwordField = UIUtils.styledPasswordField(20);
        passwordField.setName("passwordField");

        addFormRow(formPanel, gbc, 0, "Username", usernameField);
        addFormRow(formPanel, gbc, 1, "Password", passwordField);
        root.add(formPanel, BorderLayout.CENTER);

        // ── Status label ──────────────────────────────────────────────────────
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIUtils.FONT_SMALL);
        statusLabel.setForeground(UIUtils.DANGER);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // ── Login button ──────────────────────────────────────────────────────
        JButton loginBtn = UIUtils.accentButton("Sign In");
        loginBtn.setName("loginButton");
        loginBtn.setPreferredSize(new Dimension(280, 42));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(UIUtils.BG_PANEL);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(Box.createVerticalStrut(16));
        bottomPanel.add(statusLabel);
        bottomPanel.add(Box.createVerticalStrut(8));

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnWrapper.setBackground(UIUtils.BG_PANEL);
        btnWrapper.add(loginBtn);
        bottomPanel.add(btnWrapper);

        JLabel hintLabel = UIUtils.mutedLabel("Default admin: admin / admin123");
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel hintWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hintWrapper.setBackground(UIUtils.BG_PANEL);
        hintWrapper.add(hintLabel);
        bottomPanel.add(Box.createVerticalStrut(12));
        bottomPanel.add(hintWrapper);

        root.add(bottomPanel, BorderLayout.SOUTH);

        // ── Actions ───────────────────────────────────────────────────────────
        loginBtn.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());

        setContentPane(root);
        pack();
        setMinimumSize(new Dimension(400, 420));
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                            int row, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIUtils.FONT_BODY);
        label.setForeground(UIUtils.TEXT_MUTED);

        gbc.gridy = row * 2;
        gbc.gridx = 0;
        panel.add(label, gbc);

        gbc.gridy = row * 2 + 1;
        panel.add(field, gbc);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }
        try {
            User user = authService.login(username, password);
            if (user == null) {
                statusLabel.setText("Invalid username or password.");
                passwordField.setText("");
                passwordField.requestFocus();
            } else {
                authenticatedUser = user;
                dispose();
            }
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** @return the logged-in User, or null if login was cancelled. */
    public User getAuthenticatedUser() { return authenticatedUser; }
}
