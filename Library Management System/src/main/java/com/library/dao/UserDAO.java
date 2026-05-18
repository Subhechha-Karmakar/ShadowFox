package com.library.dao;

import com.library.db.DatabaseManager;
import com.library.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the users table.
 * ALL queries use PreparedStatement to prevent SQL injection.
 * ALL resources are closed via try-with-resources to prevent connection leaks.
 */
public class UserDAO {

    // ── Find by username (used during login) ─────────────────────────────────
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, full_name, email, role, created_at "
                   + "FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── Find by ID ────────────────────────────────────────────────────────────
    public User findById(int userId) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, full_name, email, role, created_at "
                   + "FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── List all users ─────────────────────────────────────────────────────
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash, full_name, email, role, created_at "
                   + "FROM users ORDER BY username";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── Insert new user ───────────────────────────────────────────────────────
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ── Update profile (email / full_name) ────────────────────────────────────
    public void updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getUserId());
            ps.executeUpdate();
        }
    }

    // ── Delete user ──────────────────────────────────────────────────────────
    public void delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ── Row mapper ────────────────────────────────────────────────────────────
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setRole(User.Role.valueOf(rs.getString("role")));
        u.setCreatedAt(rs.getString("created_at"));
        return u;
    }
}
