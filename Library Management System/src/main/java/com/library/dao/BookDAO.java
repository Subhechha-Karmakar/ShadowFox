package com.library.dao;

import com.library.db.DatabaseManager;
import com.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the books table.
 * JOINs with authors to return authorName for display.
 * All queries use PreparedStatement — no SQL injection possible.
 */
public class BookDAO {

    private static final String SELECT_BASE =
        "SELECT b.book_id, b.isbn, b.title, b.author_id, a.name AS author_name, "
        + "b.genre, b.total_copies, b.avail_copies "
        + "FROM books b JOIN authors a ON b.author_id = a.author_id ";

    // ── Find all ──────────────────────────────────────────────────────────────
    public List<Book> findAll() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY b.book_id ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── Search by title or author (LIKE, parameterized) ───────────────────────
    public List<Book> search(String keyword) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_BASE
                   + "WHERE b.title LIKE ? OR a.name LIKE ? "
                   + "ORDER BY b.book_id ASC";
        String pattern = "%" + keyword + "%";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── Find by ISBN ──────────────────────────────────────────────────────────
    public Book findByIsbn(String isbn) throws SQLException {
        String sql = SELECT_BASE + "WHERE b.isbn = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── Find by ID ────────────────────────────────────────────────────────────
    public Book findById(int bookId) throws SQLException {
        String sql = SELECT_BASE + "WHERE b.book_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── Find by genre (used for recommendations) ──────────────────────────────
    public List<Book> findByGenre(String genre, List<Integer> excludeBookIds) throws SQLException {
        List<Book> list = new ArrayList<>();
        // Build the NOT IN list safely — IDs are integers, no injection risk
        StringBuilder inClause = new StringBuilder("(0");
        for (int id : excludeBookIds) inClause.append(",").append(id);
        inClause.append(")");

        String sql = SELECT_BASE
                   + "WHERE b.genre = ? AND b.avail_copies > 0 "
                   + "AND b.book_id NOT IN " + inClause
                   + " ORDER BY b.avail_copies DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── Insert ────────────────────────────────────────────────────────────────
    public int insert(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author_id, genre, total_copies, avail_copies) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setInt(3, book.getAuthorId());
            ps.setString(4, book.getGenre());
            ps.setInt(5, book.getTotalCopies());
            ps.setInt(6, book.getAvailCopies());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ── Update available copies ───────────────────────────────────────────────
    public void updateAvailCopies(int bookId, int delta) throws SQLException {
        String sql = "UPDATE books SET avail_copies = avail_copies + ? WHERE book_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    public void delete(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        }
    }

    // ── Row mapper ────────────────────────────────────────────────────────────
    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("book_id"),
            rs.getString("isbn"),
            rs.getString("title"),
            rs.getInt("author_id"),
            rs.getString("author_name"),
            rs.getString("genre"),
            rs.getInt("total_copies"),
            rs.getInt("avail_copies")
        );
    }
}
