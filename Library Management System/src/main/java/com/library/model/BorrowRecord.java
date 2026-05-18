package com.library.model;

import java.time.LocalDate;

/**
 * Represents one borrow/return transaction.
 */
public class BorrowRecord {

    private int       recordId;
    private int       userId;
    private String    username;       // joined — not stored in table
    private int       bookId;
    private String    bookTitle;      // joined — not stored in table
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;     // null = still on loan
    private Double    fineAmount;     // null = no fine or not yet calculated

    // ── Constructors ─────────────────────────────────────────────────────────
    public BorrowRecord() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public int       getRecordId()        { return recordId; }
    public void      setRecordId(int v)   { this.recordId = v; }

    public int       getUserId()          { return userId; }
    public void      setUserId(int v)     { this.userId = v; }

    public String    getUsername()          { return username; }
    public void      setUsername(String v)  { this.username = v; }

    public int       getBookId()           { return bookId; }
    public void      setBookId(int v)      { this.bookId = v; }

    public String    getBookTitle()          { return bookTitle; }
    public void      setBookTitle(String v)  { this.bookTitle = v; }

    public LocalDate getBorrowDate()           { return borrowDate; }
    public void      setBorrowDate(LocalDate v){ this.borrowDate = v; }

    public LocalDate getDueDate()           { return dueDate; }
    public void      setDueDate(LocalDate v){ this.dueDate = v; }

    public LocalDate getReturnDate()           { return returnDate; }
    public void      setReturnDate(LocalDate v){ this.returnDate = v; }

    public Double    getFineAmount()           { return fineAmount; }
    public void      setFineAmount(Double v)   { this.fineAmount = v; }

    public boolean isReturned() { return returnDate != null; }
}
