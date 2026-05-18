package com.library.model;

/**
 * Represents a library user account.
 * Roles: ADMIN (full access) | MEMBER (borrow/return only)
 */
public class User {

    public enum Role { ADMIN, MEMBER }

    private int    userId;
    private String username;
    private String passwordHash;   // SHA-256 hex — never store plain text
    private String fullName;
    private String email;
    private Role   role;
    private String createdAt;

    // ── Constructors ────────────────────────────────────────────────────────
    public User() {}

    public User(int userId, String username, String passwordHash,
                String fullName, String email, Role role, String createdAt) {
        this.userId       = userId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.fullName     = fullName;
        this.email        = email;
        this.role         = role;
        this.createdAt    = createdAt;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public int    getUserId()       { return userId; }
    public void   setUserId(int v)  { this.userId = v; }

    public String getUsername()          { return username; }
    public void   setUsername(String v)  { this.username = v; }

    public String getPasswordHash()          { return passwordHash; }
    public void   setPasswordHash(String v)  { this.passwordHash = v; }

    public String getFullName()          { return fullName; }
    public void   setFullName(String v)  { this.fullName = v; }

    public String getEmail()          { return email; }
    public void   setEmail(String v)  { this.email = v; }

    public Role   getRole()          { return role; }
    public void   setRole(Role v)    { this.role = v; }

    public String getCreatedAt()          { return createdAt; }
    public void   setCreatedAt(String v)  { this.createdAt = v; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    @Override
    public String toString() { return fullName + " (" + username + ")"; }
}
