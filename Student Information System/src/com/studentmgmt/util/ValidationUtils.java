package com.studentmgmt.util;

import javax.swing.*;
import javax.swing.text.*;

/**
 * Input-validation helpers and custom DocumentFilters.
 */
public class ValidationUtils {

    /** Returns a DocumentFilter that only allows digits. */
    public static DocumentFilter numericFilter() {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    /** Returns a DocumentFilter that only allows digits and one decimal point. */
    public static DocumentFilter decimalFilter() {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (isValidDecimal(fb.getDocument(), offset, string)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (isValidDecimal(fb.getDocument(), offset, text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean isValidDecimal(Document doc, int offset, String text) {
                if (text == null) return true;
                try {
                    String current = doc.getText(0, doc.getLength());
                    String proposed = current.substring(0, offset) + text + current.substring(offset);
                    if (proposed.isEmpty()) return true;
                    Double.parseDouble(proposed);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        };
    }

    /** Returns a DocumentFilter that only allows phone-number characters (+, digits, spaces, hyphens). */
    public static DocumentFilter phoneFilter() {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("[\\d+\\- ]*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && text.matches("[\\d+\\- ]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }

    /** Apply numeric-only filter to a JTextField. */
    public static void applyNumericFilter(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(numericFilter());
    }

    /** Apply decimal filter to a JTextField. */
    public static void applyDecimalFilter(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(decimalFilter());
    }

    /** Apply phone filter to a JTextField. */
    public static void applyPhoneFilter(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(phoneFilter());
    }

    /** Validate that a string is a valid positive integer. */
    public static boolean isValidId(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(text.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Validate that a string is a valid mark (0-100). */
    public static boolean isValidMark(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        try {
            double val = Double.parseDouble(text.trim());
            return val >= 0 && val <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Validate that a string is a valid age (1-150). */
    public static boolean isValidAge(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        try {
            int val = Integer.parseInt(text.trim());
            return val >= 1 && val <= 150;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Validate non-empty name. */
    public static boolean isValidName(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validate DOB in DD/MM/YYYY format.
     * Allows empty (optional field).
     */
    public static boolean isValidDob(String text) {
        if (text == null || text.trim().isEmpty()) return true; // optional
        return text.trim().matches("\\d{2}/\\d{2}/\\d{4}");
    }

    /**
     * Validate mobile number — at least 7 digits, max 15.
     * Allows empty (optional field).
     */
    public static boolean isValidMobile(String text) {
        if (text == null || text.trim().isEmpty()) return true; // optional
        String digits = text.trim().replaceAll("[+\\- ]", "");
        return digits.matches("\\d{7,15}");
    }

    /**
     * Validate email address format.
     * Allows empty (optional field).
     */
    public static boolean isValidEmail(String text) {
        if (text == null || text.trim().isEmpty()) return true; // optional
        return text.trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }
}
