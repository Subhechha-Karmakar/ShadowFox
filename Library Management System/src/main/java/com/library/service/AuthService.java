package com.library.service;

import com.library.dao.UserDAO;
import com.library.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * Handles authentication: login, password hashing, and user creation.
 * Passwords are stored as SHA-256 hex digests — never in plain text.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    /**
     * Validates credentials.
     * @return the matching User, or null if credentials are wrong.
     */
    public User login(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        if (user == null) return null;
        String hashedInput = sha256(password);
        return hashedInput.equals(user.getPasswordHash()) ? user : null;
    }

    // ── Register new user ─────────────────────────────────────────────────────
    public User register(String username, String password, String fullName,
                         String email, User.Role role) throws SQLException {
        // Check for duplicate username
        if (userDAO.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(sha256(password));
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setRole(role);

        int id = userDAO.insert(newUser);
        newUser.setUserId(id);
        return newUser;
    }

    // ── Delete user ───────────────────────────────────────────────────────────
    public void deleteUser(int userId) throws SQLException {
        userDAO.delete(userId);
    }

    // ── SHA-256 hashing ───────────────────────────────────────────────────────
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public UserDAO getUserDAO() { return userDAO; }
}
