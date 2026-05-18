package com.library;

import com.library.db.DatabaseManager;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.RecommendationService;
import com.library.ui.LoginDialog;
import com.library.ui.MainFrame;
import com.library.ui.util.UIUtils;

import javax.swing.*;

/**
 * Application entry point.
 *
 * Startup sequence:
 *   1. Apply dark theme UI defaults
 *   2. Test MySQL connection
 *   3. Show login dialog
 *   4. Launch MainFrame for authenticated user
 */
public class Main {

    public static void main(String[] args) {
        // Apply dark theme before any Swing component is created
        UIUtils.applyDarkTheme();

        // Use system look-and-feel as base, then override with our dark colors
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIUtils.applyDarkTheme();  // re-apply after L&F change
        } catch (Exception ignored) {}

        // Verify database connection at startup (fail fast)
        try {
            DatabaseManager.testConnection();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                "Cannot connect to MySQL.\n\n"
                + "Please:\n"
                + "  1. Start MySQL server\n"
                + "  2. Run schema.sql\n"
                + "  3. Check credentials in DatabaseManager.java\n\n"
                + "Error: " + e.getCause().getMessage(),
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        AuthService authService = new AuthService();

        // Launch on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> showLogin(authService));
    }

    /**
     * Shows the login dialog. On success, opens the MainFrame.
     * Called on EDT. Also invoked again when the user logs out.
     */
    public static void showLogin(AuthService authService) {
        LoginDialog loginDlg = new LoginDialog(null, authService);
        loginDlg.setVisible(true);

        User user = loginDlg.getAuthenticatedUser();
        if (user == null) {
            // User closed the dialog without logging in
            System.exit(0);
        }

        // Build services
        BookService           bookService   = new BookService();
        BorrowService         borrowService = new BorrowService();
        RecommendationService recService    = new RecommendationService();

        MainFrame frame = new MainFrame(user, authService, bookService, borrowService, recService);
        frame.setVisible(true);
    }
}
