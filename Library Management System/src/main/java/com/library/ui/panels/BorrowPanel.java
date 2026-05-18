package com.library.ui.panels;

import com.library.model.BorrowRecord;
import com.library.model.Book;
import com.library.model.User;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.ui.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * 🔄 Borrow / Return Panel
 *
 * - Issue a book to a user (Admin: any user; Member: self only)
 * - Return a book — shows fine amount (Tier 1 overdue calculation)
 * - Table shows all active borrow records
 */
public class BorrowPanel extends JPanel {

    private final User          currentUser;
    private final BookService   bookService;
    private final BorrowService borrowService;

    private DefaultTableModel tableModel;
    private JTable            table;

    private static final String[] COLUMNS =
        {"Record ID", "Username", "Book Title", "Borrow Date", "Due Date", "Status"};

    public BorrowPanel(User currentUser, BookService bookService, BorrowService borrowService) {
        this.currentUser   = currentUser;
        this.bookService   = bookService;
        this.borrowService = borrowService;

        setLayout(new BorderLayout(0, 0));
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        refreshTable();
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setBackground(UIUtils.BG_DARK);
        bar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JButton borrowBtn = UIUtils.accentButton("📖 Borrow Book");
        borrowBtn.setName("borrowBookBtn");
        JButton returnBtn = UIUtils.successButton("↩ Return Book");
        returnBtn.setName("returnBookBtn");
        JButton refreshBtn = UIUtils.accentButton("⟳ Refresh");
        refreshBtn.setName("borrowRefreshBtn");

        borrowBtn.addActionListener(e -> showBorrowDialog());
        returnBtn.addActionListener(e -> returnSelectedBook());
        refreshBtn.addActionListener(e -> refreshTable());

        bar.add(borrowBtn);
        bar.add(returnBtn);
        bar.add(refreshBtn);

        return bar;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIUtils.styleTable(table);
        table.setName("borrowTable");
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        return UIUtils.darkScrollPane(table);
    }

    // ── Refresh table ─────────────────────────────────────────────────────────
    public void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<BorrowRecord> records = currentUser.isAdmin()
                ? borrowService.getAllRecords()
                : borrowService.getActiveByUser(currentUser.getUserId());

            for (BorrowRecord r : records) {
                String status = r.isReturned() ? "Returned" : "On Loan";
                tableModel.addRow(new Object[]{
                    r.getRecordId(), r.getUsername(), r.getBookTitle(),
                    r.getBorrowDate(), r.getDueDate(), status
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "DB error: " + ex.getMessage());
        }
    }

    // ── Borrow Dialog ─────────────────────────────────────────────────────────
    private void showBorrowDialog() {
        List<Book> books;
        try {
            books = bookService.getAllBooks();
        } catch (SQLException ex) {
            UIUtils.showError(this, "Cannot load books: " + ex.getMessage());
            return;
        }

        Book[] availableBooks = books.stream()
            .filter(Book::isAvailable)
            .toArray(Book[]::new);

        if (availableBooks.length == 0) {
            UIUtils.showInfo(this, "No books are currently available.");
            return;
        }

        JComboBox<Book> bookCombo = new JComboBox<>(availableBooks);
        bookCombo.setName("borrowBookCombo");
        bookCombo.setFont(UIUtils.FONT_BODY);

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.setBackground(UIUtils.BG_PANEL);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lbl = new JLabel("Select a book to borrow:");
        lbl.setFont(UIUtils.FONT_BODY);
        lbl.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lbl);
        panel.add(bookCombo);

        JLabel note = UIUtils.mutedLabel("Loan period: " + BorrowService.LOAN_DAYS + " days");
        panel.add(note);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Borrow Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;
        Book selected = (Book) bookCombo.getSelectedItem();
        if (selected == null) return;

        try {
            borrowService.borrowBook(currentUser.getUserId(), selected.getBookId());
            refreshTable();
            UIUtils.showInfo(this, "Book borrowed successfully!\nDue in " + BorrowService.LOAN_DAYS + " days.");
        } catch (Exception ex) {
            UIUtils.showError(this, ex.getMessage());
        }
    }

    // ── Return selected book ──────────────────────────────────────────────────
    private void returnSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showError(this, "Please select a borrow record to return.");
            return;
        }

        String status = (String) tableModel.getValueAt(selectedRow, 5);
        if ("Returned".equals(status)) {
            UIUtils.showError(this, "This book has already been returned.");
            return;
        }

        int recordId = (int) tableModel.getValueAt(selectedRow, 0);

        // Rebuild a lightweight BorrowRecord for the return calculation
        try {
            List<BorrowRecord> records = currentUser.isAdmin()
                ? borrowService.getAllRecords()
                : borrowService.getActiveByUser(currentUser.getUserId());

            BorrowRecord record = records.stream()
                .filter(r -> r.getRecordId() == recordId)
                .findFirst()
                .orElse(null);

            if (record == null) {
                UIUtils.showError(this, "Record not found.");
                return;
            }

            // ── Tier 1: Fine calculation ──────────────────────────────────────
            double fine = borrowService.returnBook(record);
            refreshTable();

            if (fine > 0) {
                JOptionPane.showMessageDialog(this,
                    String.format("Book returned.\n\n⚠ Overdue Fine: ₹%.2f\n(Please pay at the counter.)", fine),
                    "Return — Overdue Fine",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                UIUtils.showInfo(this, "Book returned successfully! No fine.");
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Return failed: " + ex.getMessage());
        }
    }
}
