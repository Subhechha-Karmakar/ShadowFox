package com.library.service;

import com.library.dao.BorrowDAO;
import com.library.model.Book;
import com.library.service.BookService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates book recommendations for a member.
 *
 * Algorithm:
 *   1. Find all genres the user has borrowed in the past.
 *   2. Find all book IDs the user has ever borrowed.
 *   3. For each genre, fetch available books NOT already borrowed by the user.
 *   4. Return up to MAX_RESULTS unique recommendations.
 */
public class RecommendationService {

    private static final int MAX_RESULTS = 10;

    private final BorrowDAO  borrowDAO;
    private final BookService bookService;

    public RecommendationService() {
        this.borrowDAO   = new BorrowDAO();
        this.bookService = new BookService();
    }

    public List<Book> getRecommendations(int userId) throws SQLException {
        List<String>  genres       = borrowDAO.findBorrowedGenresByUser(userId);
        List<Integer> borrowedIds  = borrowDAO.findBorrowedBookIdsByUser(userId);

        Set<Integer> seenIds = new LinkedHashSet<>();
        List<Book>   results = new ArrayList<>();

        for (String genre : genres) {
            List<Book> candidates = bookService.findByGenre(genre, borrowedIds);
            for (Book b : candidates) {
                if (!seenIds.contains(b.getBookId())) {
                    seenIds.add(b.getBookId());
                    results.add(b);
                    if (results.size() >= MAX_RESULTS) return results;
                }
            }
        }

        // If no history yet, return all available books sorted by availability
        if (results.isEmpty()) {
            List<Book> allBooks = bookService.getAllBooks();
            for (Book b : allBooks) {
                if (b.isAvailable()) {
                    results.add(b);
                    if (results.size() >= MAX_RESULTS) break;
                }
            }
        }

        return results;
    }
}
