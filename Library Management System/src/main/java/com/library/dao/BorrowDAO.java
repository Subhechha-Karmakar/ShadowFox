package com.library.dao;

import com.library.db.DatabaseManager;
import com.library.model.BorrowRecord;
import com.library.model.Fine;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for borrow_records and fines tables.
 * Uses PreparedStatement throughout and try-with-resources for all DB resources.
 */
public class BorrowDAO {

    private static final String SELECT_RECORD =
        "SELECT br.record_id, br.user_id, u.username, br.book_id, b.title, "
        + "br.borrow_date, br.due_date, br.return_date, f.amount "
        + "FROM borrow_records br "
        + "JOIN users u ON br.user_id = u.user_id "
        + "JOIN books b ON br.book_id = b.book_id "
        + "LEFT JOIN fines f ON br.record_id = f.record_id ";

    // ── Active borrows for a user ─────────────────────────────────────────────
    public List<BorrowRecord> findActiveByUser(int userId) throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = SELECT_RECORD + "WHERE br.user_id = ? AND br.return_date IS NULL ORDER BY br.due_date";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── All borrows (admin view) ──────────────────────────────────────────────
    public List<BorrowRecord> findAll() throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = SELECT_RECORD + "ORDER BY br.borrow_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── History for a specific user (returned + active) ───────────────────────
    public List<BorrowRecord> findHistoryByUser(int userId) throws SQLException {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = SELECT_RECORD + "WHERE br.user_id = ? ORDER BY br.borrow_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── Distinct genres borrowed by a user (for recommendations) ─────────────
    public List<String> findBorrowedGenresByUser(int userId) throws SQLException {
        List<String> genres = new ArrayList<>();
        String sql = "SELECT DISTINCT bk.genre FROM borrow_records br "
                   + "JOIN books bk ON br.book_id = bk.book_id "
                   + "WHERE br.user_id = ? AND bk.genre IS NOT NULL";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) genres.add(rs.getString(1));
            }
        }
        return genres;
    }

    // ── Book IDs already borrowed by a user ───────────────────────────────────
    public List<Integer> findBorrowedBookIdsByUser(int userId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT DISTINCT book_id FROM borrow_records WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        }
        return ids;
    }

    // ── Insert borrow record ──────────────────────────────────────────────────
    public int insert(BorrowRecord record) throws SQLException {
        String sql = "INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, record.getUserId());
            ps.setInt(2, record.getBookId());
            ps.setDate(3, Date.valueOf(record.getBorrowDate()));
            ps.setDate(4, Date.valueOf(record.getDueDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ── Mark as returned ──────────────────────────────────────────────────────
    public void markReturned(int recordId, LocalDate returnDate) throws SQLException {
        String sql = "UPDATE borrow_records SET return_date = ? WHERE record_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(returnDate));
            ps.setInt(2, recordId);
            ps.executeUpdate();
        }
    }

    // ── Insert fine ───────────────────────────────────────────────────────────
    public void insertFine(int recordId, double amount) throws SQLException {
        String sql = "INSERT INTO fines (record_id, amount, paid) VALUES (?, ?, FALSE) "
                   + "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        }
    }

    // ── Mark fine paid ────────────────────────────────────────────────────────
    public void markFinePaid(int recordId) throws SQLException {
        String sql = "UPDATE fines SET paid = TRUE WHERE record_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ps.executeUpdate();
        }
    }

    // ── Unpaid fines for a user ────────────────────────────────────────────────
    public List<Fine> findUnpaidFinesByUser(int userId) throws SQLException {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT f.fine_id, f.record_id, f.amount, f.paid "
                   + "FROM fines f JOIN borrow_records br ON f.record_id = br.record_id "
                   + "WHERE br.user_id = ? AND f.paid = FALSE";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Fine fine = new Fine(
                        rs.getInt("fine_id"),
                        rs.getInt("record_id"),
                        rs.getDouble("amount"),
                        rs.getBoolean("paid")
                    );
                    fines.add(fine);
                }
            }
        }
        return fines;
    }

    // ── Row mapper ────────────────────────────────────────────────────────────
    private BorrowRecord mapRow(ResultSet rs) throws SQLException {
        BorrowRecord rec = new BorrowRecord();
        rec.setRecordId(rs.getInt("record_id"));
        rec.setUserId(rs.getInt("user_id"));
        rec.setUsername(rs.getString("username"));
        rec.setBookId(rs.getInt("book_id"));
        rec.setBookTitle(rs.getString("title"));
        rec.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        rec.setDueDate(rs.getDate("due_date").toLocalDate());
        Date retDate = rs.getDate("return_date");
        if (retDate != null) rec.setReturnDate(retDate.toLocalDate());
        double amount = rs.getDouble("amount");
        if (!rs.wasNull()) rec.setFineAmount(amount);
        return rec;
    }
}
