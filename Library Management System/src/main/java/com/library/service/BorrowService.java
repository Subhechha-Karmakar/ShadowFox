package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.BorrowDAO;
import com.library.model.Book;
import com.library.model.BorrowRecord;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * ── TIER 1 UPGRADE ──────────────────────────────────────────────────────────
 *
 * Overdue Fine Calculation using java.time.LocalDate:
 *
 *   overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate)
 *   fine        = overdueDays * FINE_RATE_PER_DAY   (if overdueDays > 0)
 *
 * Fine rate is ₹5.00/day (configurable via FINE_RATE_PER_DAY constant).
 * Fine is persisted in the fines table on book return.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class BorrowService {

    /** Fine amount charged per overdue day (in ₹). */
    public static final double FINE_RATE_PER_DAY = 5.00;

    /** Default loan duration in days when borrowing a book. */
    public static final int LOAN_DAYS = 14;

    private final BorrowDAO borrowDAO;
    private final BookDAO   bookDAO;

    public BorrowService() {
        this.borrowDAO = new BorrowDAO();
        this.bookDAO   = new BookDAO();
    }

    // ── Borrow a book ─────────────────────────────────────────────────────────
    /**
     * Issues a book to a user. Decrements avail_copies atomically.
     * @throws IllegalStateException if no copies are available
     */
    public BorrowRecord borrowBook(int userId, int bookId) throws SQLException {
        Book book = bookDAO.findById(bookId);
        if (book == null)          throw new IllegalArgumentException("Book not found.");
        if (!book.isAvailable())   throw new IllegalStateException("No available copies of \"" + book.getTitle() + "\".");

        LocalDate today   = LocalDate.now();
        LocalDate dueDate = today.plusDays(LOAN_DAYS);

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(today);
        record.setDueDate(dueDate);

        int recordId = borrowDAO.insert(record);
        record.setRecordId(recordId);

        bookDAO.updateAvailCopies(bookId, -1);   // decrement
        return record;
    }

    // ── Return a book ─────────────────────────────────────────────────────────
    /**
     * Marks a borrow record as returned and calculates any overdue fine.
     *
     * Fine calculation (Tier 1):
     *   long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
     *   if (overdueDays > 0) fine = overdueDays * FINE_RATE_PER_DAY;
     *
     * @return the fine amount in ₹ (0.0 if returned on time)
     */
    public double returnBook(BorrowRecord record) throws SQLException {
        LocalDate returnDate = LocalDate.now();

        // ── Tier 1: Overdue calculation ─────────────────────────────────────
        long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
        double fine = (overdueDays > 0) ? overdueDays * FINE_RATE_PER_DAY : 0.0;

        borrowDAO.markReturned(record.getRecordId(), returnDate);

        if (fine > 0) {
            borrowDAO.insertFine(record.getRecordId(), fine);
        }

        bookDAO.updateAvailCopies(record.getBookId(), +1);   // increment

        return fine;
    }

    // ── Queries ───────────────────────────────────────────────────────────────
    public List<BorrowRecord> getActiveByUser(int userId) throws SQLException {
        return borrowDAO.findActiveByUser(userId);
    }

    public List<BorrowRecord> getHistory(int userId) throws SQLException {
        return borrowDAO.findHistoryByUser(userId);
    }

    public List<BorrowRecord> getAllRecords() throws SQLException {
        return borrowDAO.findAll();
    }

    public void markFinePaid(int recordId) throws SQLException {
        borrowDAO.markFinePaid(recordId);
    }

    public BorrowDAO getBorrowDAO() { return borrowDAO; }
}
