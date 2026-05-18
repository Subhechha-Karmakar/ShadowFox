package com.library.ui;

import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.RecommendationService;
import com.library.ui.panels.BookPanel;
import com.library.ui.panels.BorrowPanel;
import com.library.ui.panels.RecommendationPanel;
import com.library.ui.panels.UserPanel;
import com.library.ui.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application window. Hosts a JTabbedPane with role-based tabs:
 *
 *   📚 Books          — All users (Add/Delete: Admin only)
 *   🔄 Borrow/Return  — All users
 *   👤 My Account     — Member view (history, unpaid fines)
 *   👥 Users          — Admin only
 *   ⭐ Recommendations — Member only
 */
public class MainFrame extends JFrame {

    private final User                currentUser;
    private final AuthService         authService;
    private final BookService         bookService;
    private final BorrowService       borrowService;
    private final RecommendationService recService;

    public MainFrame(User currentUser,
                     AuthService authService,
                     BookService bookService,
                     BorrowService borrowService,
                     RecommendationService recService) {
        this.currentUser   = currentUser;
        this.authService   = authService;
        this.bookService   = bookService;
        this.borrowService = borrowService;
        this.recService    = recService;

        buildUI();
    }

    private void buildUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        getContentPane().setBackground(UIUtils.BG_DARK);
        setLayout(new BorderLayout());

        // ── Header bar ────────────────────────────────────────────────────────
        JPanel header = buildHeader();
        add(header, BorderLayout.NORTH);

        // ── Tabbed pane ───────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setBackground(UIUtils.BG_DARK);
        tabs.setForeground(new Color(15, 23, 42));  // near-black dark
        tabs.setFont(UIUtils.FONT_TITLE);
        tabs.setBorder(new EmptyBorder(0, 0, 0, 0));

        tabs.addTab("📚 Books",
            new BookPanel(currentUser, bookService));
        tabs.addTab("🔄 Borrow / Return",
            new BorrowPanel(currentUser, bookService, borrowService));
        tabs.addTab("👤 My Account",
            new com.library.ui.panels.AccountPanel(currentUser, borrowService));

        if (currentUser.isAdmin()) {
            tabs.addTab("👥 Users",
                new UserPanel(authService));
        }

        tabs.addTab("⭐ Recommendations",
            new RecommendationPanel(currentUser, recService));

        add(tabs, BorderLayout.CENTER);

        // ── Status bar ────────────────────────────────────────────────────────
        JPanel statusBar = buildStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.BG_PANEL);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, UIUtils.ACCENT),
            new EmptyBorder(12, 20, 12, 20)
        ));

        // Left: logo + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(UIUtils.BG_PANEL);

        JLabel logoIcon = new JLabel("📚");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);

        left.add(logoIcon);
        left.add(titleLabel);

        // Right: user info + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(UIUtils.BG_PANEL);

        String roleText = currentUser.isAdmin() ? "🛡 Admin" : "👤 Member";
        JLabel userLabel = new JLabel(roleText + "  |  " + currentUser.getFullName());
        userLabel.setFont(UIUtils.FONT_BODY);
        userLabel.setForeground(UIUtils.TEXT_MUTED);

        JButton logoutBtn = UIUtils.dangerButton("Logout");
        logoutBtn.setName("logoutButton");
        logoutBtn.addActionListener(e -> {
            int confirm = UIUtils.showDarkConfirm(this,
                "Are you sure you want to logout?", "Confirm Logout");
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                // Re-launch login
                SwingUtilities.invokeLater(() ->
                    com.library.Main.showLogin(authService));
            }
        });

        right.add(userLabel);
        right.add(logoutBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Status bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        bar.setBackground(UIUtils.BG_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER));

        JLabel connLabel = new JLabel("● Connected to MySQL");
        connLabel.setFont(UIUtils.FONT_SMALL);
        connLabel.setForeground(UIUtils.SUCCESS);

        JLabel fineInfo = UIUtils.mutedLabel("Fine rate: ₹" + BorrowService.FINE_RATE_PER_DAY + "/day  |  Loan period: " + BorrowService.LOAN_DAYS + " days");

        bar.add(connLabel);
        bar.add(Box.createHorizontalStrut(20));
        bar.add(fineInfo);
        return bar;
    }
}
