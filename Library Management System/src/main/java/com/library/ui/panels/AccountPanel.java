package com.library.ui.panels;

import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.service.BorrowService;
import com.library.ui.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * 👤 My Account Panel
 * Shows the current member's profile, complete borrow history,
 * and any unpaid fines.
 */
public class AccountPanel extends JPanel {

    private final User          currentUser;
    private final BorrowService borrowService;

    private DefaultTableModel historyModel;

    private static final String[] HISTORY_COLS =
        {"Record ID", "Book", "Borrowed", "Due", "Returned", "Fine (₹)"};

    public AccountPanel(User currentUser, BorrowService borrowService) {
        this.currentUser   = currentUser;
        this.borrowService = borrowService;

        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildProfileCard(), BorderLayout.NORTH);
        add(buildHistorySection(), BorderLayout.CENTER);

        refreshHistory();
    }

    // ── Profile card ──────────────────────────────────────────────────────────
    private JPanel buildProfileCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIUtils.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.ACCENT),
            new EmptyBorder(20, 24, 20, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 8, 4, 24);

        JLabel avatar = new JLabel(currentUser.isAdmin() ? "🛡" : "👤");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 3;
        card.add(avatar, gbc);

        gbc.gridheight = 1;
        gbc.gridx = 1;

        addProfileRow(card, gbc, 0, "Full Name:", currentUser.getFullName());
        addProfileRow(card, gbc, 1, "Username:",  currentUser.getUsername());
        addProfileRow(card, gbc, 2, "Email:",     currentUser.getEmail());
        addProfileRow(card, gbc, 3, "Role:",      currentUser.getRole().name());
        addProfileRow(card, gbc, 4, "Member Since:", currentUser.getCreatedAt());

        return card;
    }

    private void addProfileRow(JPanel panel, GridBagConstraints gbc,
                               int row, String label, String value) {
        gbc.gridy = row;
        gbc.gridx = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_BODY);
        lbl.setForeground(UIUtils.TEXT_MUTED);
        panel.add(lbl, gbc);

        gbc.gridx = 2;
        JLabel val = new JLabel(value != null ? value : "—");
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(val, gbc);
    }

    // ── History section ───────────────────────────────────────────────────────
    private JPanel buildHistorySection() {
        JPanel section = new JPanel(new BorderLayout(0, 8));
        section.setBackground(UIUtils.BG_DARK);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setBackground(UIUtils.BG_DARK);
        JLabel title = UIUtils.headerLabel("📋 Borrow History");
        title.setForeground(UIUtils.TEXT_PRIMARY);   // light color override
        JButton refreshBtn = UIUtils.accentButton("⟳ Refresh");
        refreshBtn.setName("accountRefreshBtn");
        refreshBtn.addActionListener(e -> refreshHistory());
        header.add(title);
        header.add(Box.createHorizontalStrut(16));
        header.add(refreshBtn);

        historyModel = new DefaultTableModel(HISTORY_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyTable = new JTable(historyModel);
        UIUtils.styleTable(historyTable);
        historyTable.setName("historyTable");

        section.add(header, BorderLayout.NORTH);
        section.add(UIUtils.darkScrollPane(historyTable), BorderLayout.CENTER);
        return section;
    }

    // ── Refresh history ───────────────────────────────────────────────────────
    public void refreshHistory() {
        historyModel.setRowCount(0);
        try {
            List<BorrowRecord> history = borrowService.getHistory(currentUser.getUserId());
            for (BorrowRecord r : history) {
                historyModel.addRow(new Object[]{
                    r.getRecordId(),
                    r.getBookTitle(),
                    r.getBorrowDate(),
                    r.getDueDate(),
                    r.getReturnDate() != null ? r.getReturnDate().toString() : "On Loan",
                    r.getFineAmount() != null ? String.format("%.2f", r.getFineAmount()) : "—"
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Failed to load history: " + ex.getMessage());
        }
    }
}
