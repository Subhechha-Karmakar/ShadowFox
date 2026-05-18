package com.library.model;

/**
 * Represents a book author.
 * Normalized: stored in its own table — NOT duplicated in the books table.
 */
public class Author {

    private int    authorId;
    private String name;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Author() {}

    public Author(int authorId, String name) {
        this.authorId = authorId;
        this.name     = name;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public int    getAuthorId()       { return authorId; }
    public void   setAuthorId(int v)  { this.authorId = v; }

    public String getName()          { return name; }
    public void   setName(String v)  { this.name = v; }

    @Override
    public String toString() { return name; }
}
