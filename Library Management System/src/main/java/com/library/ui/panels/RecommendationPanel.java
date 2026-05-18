package com.library.ui.panels;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.RecommendationService;
import com.library.ui.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * ⭐ Recommendations Panel
 * Shows genre-based book recommendations for the current user.
 * If the user has no borrow history, shows all available books.
 */
public class RecommendationPanel extends JPanel {

    private final User                currentUser;
    private final RecommendationService recService;

    private DefaultTableModel tableModel;

    private static final String[] COLUMNS =
        {"ID", "Title", "Author", "Genre", "Available"};

    public RecommendationPanel(User currentUser, RecommendationService recService) {
        this.currentUser = currentUser;
        this.recService  = recService;

        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        loadRecommendations();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.BG_DARK);

        JLabel title = UIUtils.headerLabel("⭐ Book Recommendations");
        JLabel desc  = UIUtils.mutedLabel(
            "Personalized suggestions based on your reading history.");

        JButton refreshBtn = UIUtils.accentButton("⟳ Refresh");
        refreshBtn.setName("recRefreshBtn");
        refreshBtn.addActionListener(e -> loadRecommendations());

        JPanel left = new JPanel();
        left.setBackground(UIUtils.BG_DARK);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(desc);

        panel.add(left, BorderLayout.WEST);
        panel.add(refreshBtn, BorderLayout.EAST);
        return panel;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        UIUtils.styleTable(table);
        table.setName("recommendationsTable");
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(4).setMaxWidth(80);
        return UIUtils.darkScrollPane(table);
    }

    // ── Load data ─────────────────────────────────────────────────────────────
    public void loadRecommendations() {
        tableModel.setRowCount(0);
        try {
            List<Book> recs = recService.getRecommendations(currentUser.getUserId());
            if (recs.isEmpty()) {
                tableModel.addRow(new Object[]{"", "No recommendations available.", "", "", ""});
            } else {
                for (Book b : recs) {
                    tableModel.addRow(new Object[]{
                        b.getBookId(), b.getTitle(), b.getAuthorName(),
                        b.getGenre(), b.getAvailCopies()
                    });
                }
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Failed to load recommendations: " + ex.getMessage());
        }
    }
}
