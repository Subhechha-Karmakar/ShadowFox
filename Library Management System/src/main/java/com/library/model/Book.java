package com.library.model;

/**
 * Represents a book in the library catalog.
 *
 * NOTE: authorName is a transient display field (joined from authors table).
 *       The actual FK stored in DB is authorId.
 */
public class Book {

    private int    bookId;
    private String isbn;
    private String title;
    private int    authorId;
    private String authorName;   // joined from authors — NOT stored in books table
    private String genre;
    private int    totalCopies;
    private int    availCopies;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Book() {}

    public Book(int bookId, String isbn, String title,
                int authorId, String authorName,
                String genre, int totalCopies, int availCopies) {
        this.bookId      = bookId;
        this.isbn        = isbn;
        this.title       = title;
        this.authorId    = authorId;
        this.authorName  = authorName;
        this.genre       = genre;
        this.totalCopies = totalCopies;
        this.availCopies = availCopies;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public int    getBookId()        { return bookId; }
    public void   setBookId(int v)   { this.bookId = v; }

    public String getIsbn()          { return isbn; }
    public void   setIsbn(String v)  { this.isbn = v; }

    public String getTitle()          { return title; }
    public void   setTitle(String v)  { this.title = v; }

    public int    getAuthorId()       { return authorId; }
    public void   setAuthorId(int v)  { this.authorId = v; }

    public String getAuthorName()          { return authorName; }
    public void   setAuthorName(String v)  { this.authorName = v; }

    public String getGenre()          { return genre; }
    public void   setGenre(String v)  { this.genre = v; }

    public int    getTotalCopies()       { return totalCopies; }
    public void   setTotalCopies(int v)  { this.totalCopies = v; }

    public int    getAvailCopies()       { return availCopies; }
    public void   setAvailCopies(int v)  { this.availCopies = v; }

    public boolean isAvailable() { return availCopies > 0; }

    @Override
    public String toString() { return title + " — " + authorName; }
}
