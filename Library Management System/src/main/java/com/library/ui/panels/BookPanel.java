package com.library.ui.panels;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.BookService;
import com.library.service.GoogleBooksService;
import com.library.ui.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * 📚 Books Panel
 * - Shows all books in a searchable table
 * - Admin: Add book (with ISBN lookup from Google Books API), Delete book
 * - Member: Browse and search only
 */
public class BookPanel extends JPanel {

    private final User        currentUser;
    private final BookService bookService;
    private final GoogleBooksService googleBooksService;

    private DefaultTableModel tableModel;
    private JTable            table;
    private JTextField        searchField;

    private static final String[] COLUMNS =
        {"ID", "ISBN", "Title", "Author", "Genre", "Total", "Available"};

    public BookPanel(User currentUser, BookService bookService) {
        this.currentUser        = currentUser;
        this.bookService        = bookService;
        this.googleBooksService = new GoogleBooksService();

        setLayout(new BorderLayout(0, 0));
        setBackground(UIUtils.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);

        refreshTable(null);
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(UIUtils.BG_DARK);
        bar.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setBackground(UIUtils.BG_DARK);

        JLabel searchIcon = new JLabel("🔍");
        searchField = UIUtils.styledField(25);
        searchField.setName("bookSearchField");
        JButton searchBtn = UIUtils.accentButton("Search");
        searchBtn.setName("bookSearchBtn");
        JButton clearBtn  = UIUtils.successButton("All Books");
        clearBtn.setName("bookClearBtn");

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        searchBtn.addActionListener(e -> refreshTable(searchField.getText().trim()));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            refreshTable(null);
        });
        searchField.addActionListener(e -> refreshTable(searchField.getText().trim()));

        bar.add(searchPanel, BorderLayout.WEST);

        // Admin buttons
        if (currentUser.isAdmin()) {
            JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            adminPanel.setBackground(UIUtils.BG_DARK);

            JButton addBtn = UIUtils.accentButton("＋ Add Book");
            addBtn.setName("addBookBtn");
            JButton delBtn = UIUtils.dangerButton("🗑 Delete");
            delBtn.setName("deleteBookBtn");

            addBtn.addActionListener(e -> showAddBookDialog());
            delBtn.addActionListener(e -> deleteSelectedBook());

            adminPanel.add(addBtn);
            adminPanel.add(delBtn);
            bar.add(adminPanel, BorderLayout.EAST);
        }

        return bar;
    }

    // ── Table ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIUtils.styleTable(table);
        table.setName("booksTable");
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(5).setMaxWidth(60);
        table.getColumnModel().getColumn(6).setMaxWidth(80);
        return UIUtils.darkScrollPane(table);
    }

    // ── Load / refresh data ───────────────────────────────────────────────────
    public void refreshTable(String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Book> books = (keyword == null || keyword.isEmpty())
                ? bookService.getAllBooks()
                : bookService.search(keyword);
            for (Book b : books) {
                tableModel.addRow(new Object[]{
                    b.getBookId(), b.getIsbn(), b.getTitle(),
                    b.getAuthorName(), b.getGenre(),
                    b.getTotalCopies(), b.getAvailCopies()
                });
            }
        } catch (SQLException ex) {
            UIUtils.showError(this, "Failed to load books: " + ex.getMessage());
        }
    }

    // ── Add Book dialog ───────────────────────────────────────────────────────
    private void showAddBookDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Add New Book", true);
        dlg.getContentPane().setBackground(UIUtils.BG_PANEL);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.BG_PANEL);
        panel.setBorder(new EmptyBorder(24, 30, 24, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JTextField isbnField   = UIUtils.styledField(22);
        isbnField.setName("isbnField");
        JTextField titleField  = UIUtils.styledField(22);
        titleField.setName("titleField");
        JTextField authorField = UIUtils.styledField(22);
        authorField.setName("authorField");
        JTextField genreField  = UIUtils.styledField(22);
        genreField.setName("genreField");
        JSpinner   copiesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        copiesSpinner.setName("copiesSpinner");
        copiesSpinner.setFont(UIUtils.FONT_BODY);

        JButton lookupBtn = UIUtils.accentButton("🔍 Lookup ISBN");
        lookupBtn.setName("isbnLookupBtn");
        JLabel lookupStatus = UIUtils.mutedLabel(" ");

        // ── Tier 2: Google Books API lookup ──────────────────────────────────
        lookupBtn.addActionListener(e -> {
            String isbn = isbnField.getText().trim();
            if (isbn.isEmpty()) {
                lookupStatus.setText("Enter an ISBN first.");
                lookupStatus.setForeground(UIUtils.WARNING);
                return;
            }
            lookupStatus.setText("Searching Google Books…");
            lookupStatus.setForeground(UIUtils.TEXT_MUTED);

            SwingWorker<GoogleBooksService.BookInfo, Void> worker = new SwingWorker<>() {
                @Override protected GoogleBooksService.BookInfo doInBackground() {
                    return googleBooksService.fetchByIsbn(isbn);
                }
                @Override protected void done() {
                    try {
                        GoogleBooksService.BookInfo info = get();
                        if (info == null) {
                            lookupStatus.setText("Book not found on Google Books.");
                            lookupStatus.setForeground(UIUtils.DANGER);
                        } else {
                            titleField.setText(info.title());
                            authorField.setText(info.author());
                            lookupStatus.setText("✓ Found: " + info.title());
                            lookupStatus.setForeground(UIUtils.SUCCESS);
                        }
                    } catch (Exception ex) {
                        lookupStatus.setText("Lookup failed: " + ex.getMessage());
                        lookupStatus.setForeground(UIUtils.DANGER);
                    }
                }
            };
            worker.execute();
        });

        // Layout rows
        int row = 0;
        addDialogRow(panel, gbc, row++, "ISBN:", isbnField);
        gbc.gridy = row++;
        gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel lookupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lookupPanel.setBackground(UIUtils.BG_PANEL);
        lookupPanel.add(lookupBtn);
        lookupPanel.add(Box.createHorizontalStrut(10));
        lookupPanel.add(lookupStatus);
        panel.add(lookupPanel, gbc);
        gbc.gridwidth = 1;

        addDialogRow(panel, gbc, row++, "Title *:", titleField);
        addDialogRow(panel, gbc, row++, "Author *:", authorField);
        addDialogRow(panel, gbc, row++, "Genre:", genreField);
        addDialogRow(panel, gbc, row++, "Copies:", copiesSpinner);

        // Buttons
        JButton saveBtn   = UIUtils.accentButton("Save");
        saveBtn.setName("saveBookBtn");
        JButton cancelBtn = UIUtils.dangerButton("Cancel");
        cancelBtn.setName("cancelBookBtn");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(UIUtils.BG_PANEL);
        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 0, 0, 0);
        panel.add(btnRow, gbc);

        saveBtn.addActionListener(e -> {
            try {
                bookService.addBook(
                    isbnField.getText().trim(),
                    titleField.getText().trim(),
                    authorField.getText().trim(),
                    genreField.getText().trim(),
                    (Integer) copiesSpinner.getValue()
                );
                dlg.dispose();
                refreshTable(null);
            } catch (IllegalArgumentException iae) {
                UIUtils.showError(dlg, iae.getMessage());
            } catch (SQLException ex) {
                UIUtils.showError(dlg, "DB error: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dlg.dispose());

        dlg.setContentPane(panel);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(440, 380));
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private void addDialogRow(JPanel panel, GridBagConstraints gbc,
                              int row, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.FONT_BODY);
        lbl.setForeground(UIUtils.TEXT_MUTED);

        gbc.gridy = row * 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(lbl, gbc);

        gbc.gridy = row * 2 + 1;
        panel.add(field, gbc);
    }

    // ── Delete selected book ──────────────────────────────────────────────────
    private void deleteSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showDarkError(this, "Please select a book to delete.");
            return;
        }
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = UIUtils.showDarkConfirm(this,
            "Delete book: \"" + title + "\"?", "Confirm Delete");
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            bookService.deleteBook(bookId);
            refreshTable(null);
        } catch (SQLException ex) {
            UIUtils.showError(this, "Cannot delete: " + ex.getMessage());
        }
    }
}
