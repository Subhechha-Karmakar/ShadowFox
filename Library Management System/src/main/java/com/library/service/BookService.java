package com.library.service;

import com.library.dao.AuthorDAO;
import com.library.dao.BookDAO;
import com.library.model.Author;
import com.library.model.Book;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for book catalog management.
 * Delegates persistence to BookDAO and AuthorDAO.
 */
public class BookService {

    private final BookDAO  bookDAO;
    private final AuthorDAO authorDAO;

    public BookService() {
        this.bookDAO   = new BookDAO();
        this.authorDAO = new AuthorDAO();
    }

    // ── List all books ────────────────────────────────────────────────────────
    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.findAll();
    }

    // ── Search ────────────────────────────────────────────────────────────────
    public List<Book> search(String keyword) throws SQLException {
        return bookDAO.search(keyword);
    }

    // ── Add book ──────────────────────────────────────────────────────────────
    /**
     * Adds a new book. If the author name doesn't exist in the authors table,
     * a new author record is created (normalization).
     */
    public Book addBook(String isbn, String title, String authorName,
                        String genre, int copies) throws SQLException {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be empty.");
        if (authorName == null || authorName.isBlank())
            throw new IllegalArgumentException("Author name cannot be empty.");
        if (copies < 1)
            throw new IllegalArgumentException("Copies must be at least 1.");

        Author author = authorDAO.findOrCreate(authorName.trim());

        Book book = new Book();
        book.setIsbn(isbn != null && !isbn.isBlank() ? isbn.trim() : null);
        book.setTitle(title.trim());
        book.setAuthorId(author.getAuthorId());
        book.setAuthorName(author.getName());
        book.setGenre(genre != null && !genre.isBlank() ? genre.trim() : null);
        book.setTotalCopies(copies);
        book.setAvailCopies(copies);

        int id = bookDAO.insert(book);
        book.setBookId(id);
        return book;
    }

    // ── Delete book ───────────────────────────────────────────────────────────
    public void deleteBook(int bookId) throws SQLException {
        bookDAO.delete(bookId);
    }

    // ── Find by ISBN ──────────────────────────────────────────────────────────
    public Book findByIsbn(String isbn) throws SQLException {
        return bookDAO.findByIsbn(isbn);
    }

    // ── Find by ID ────────────────────────────────────────────────────────────
    public Book findById(int bookId) throws SQLException {
        return bookDAO.findById(bookId);
    }

    // ── Genre-based lookup (for recommendations) ──────────────────────────────
    public List<Book> findByGenre(String genre, List<Integer> excludeIds) throws SQLException {
        return bookDAO.findByGenre(genre, excludeIds);
    }

    public BookDAO getBookDAO() { return bookDAO; }
}
