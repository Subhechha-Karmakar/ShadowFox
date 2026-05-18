package com.library.dao;

import com.library.db.DatabaseManager;
import com.library.model.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the authors table.
 * Provides find-or-create semantics to support deduplication.
 */
public class AuthorDAO {

    // ── Find by exact name ───────────────────────────────────────────────────
    public Author findByName(String name) throws SQLException {
        String sql = "SELECT author_id, name FROM authors WHERE name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── Find by ID ────────────────────────────────────────────────────────────
    public Author findById(int authorId) throws SQLException {
        String sql = "SELECT author_id, name FROM authors WHERE author_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── List all ─────────────────────────────────────────────────────────────
    public List<Author> findAll() throws SQLException {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT author_id, name FROM authors ORDER BY name";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── Insert ────────────────────────────────────────────────────────────────
    public int insert(String name) throws SQLException {
        String sql = "INSERT INTO authors (name) VALUES (?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Returns an existing author with this name, or creates a new one.
     * Useful when adding books — avoids duplicating author records.
     */
    public Author findOrCreate(String name) throws SQLException {
        Author existing = findByName(name);
        if (existing != null) return existing;
        int id = insert(name);
        return new Author(id, name);
    }

    // ── Row mapper ────────────────────────────────────────────────────────────
    private Author mapRow(ResultSet rs) throws SQLException {
        return new Author(rs.getInt("author_id"), rs.getString("name"));
    }
}
