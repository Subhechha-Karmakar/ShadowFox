package com.studentmgmt.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

/**
 * Dark-themed Swing component factory and theme installer.
 * Provides a modern, premium dark UI throughout the application.
 */
public class ThemeManager {

    // ── Color palette ───────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(15, 17, 23);
    public static final Color BG_SURFACE    = new Color(22, 27, 34);
    public static final Color BG_CARD       = new Color(33, 38, 45);
    public static final Color BG_ELEVATED   = new Color(48, 54, 61);
    public static final Color BORDER_COLOR  = new Color(48, 54, 61);

    public static final Color TEXT_PRIMARY   = new Color(230, 237, 243);
    public static final Color TEXT_SECONDARY = new Color(139, 148, 158);
    public static final Color TEXT_MUTED     = new Color(110, 118, 129);

    public static final Color ACCENT_BLUE    = new Color(88, 166, 255);
    public static final Color ACCENT_GREEN   = new Color(63, 185, 80);
    public static final Color ACCENT_RED     = new Color(248, 81, 73);
    public static final Color ACCENT_PURPLE  = new Color(188, 140, 255);
    public static final Color ACCENT_ORANGE  = new Color(210, 153, 34);
    public static final Color ACCENT_TEAL    = new Color(63, 185, 175);

    public static final Color TABLE_HEADER   = new Color(22, 27, 34);
    public static final Color TABLE_ROW_ALT  = new Color(22, 27, 34);
    public static final Color TABLE_ROW      = new Color(13, 17, 23);
    public static final Color TABLE_SELECT   = new Color(30, 60, 110);

    public static final Font FONT_TITLE      = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE   = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY       = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL      = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO       = new Font("Consolas", Font.PLAIN, 13);
    public static final Font FONT_BUTTON     = new Font("Segoe UI", Font.BOLD, 12);

    // ── Theme installer ─────────────────────────────────────────────

    public static void install() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());

            UIManager.put("control",                BG_SURFACE);
            UIManager.put("info",                   BG_CARD);
            UIManager.put("nimbusBase",             new Color(18, 30, 49));
            UIManager.put("nimbusDisabledText",     TEXT_MUTED);
            UIManager.put("nimbusFocus",            ACCENT_BLUE);
            UIManager.put("nimbusGreen",            ACCENT_GREEN);
            UIManager.put("nimbusInfoBlue",         ACCENT_BLUE);
            UIManager.put("nimbusLightBackground",  BG_DARK);
            UIManager.put("nimbusOrange",           ACCENT_ORANGE);
            UIManager.put("nimbusRed",              ACCENT_RED);
            UIManager.put("nimbusSelectedText",     Color.WHITE);
            UIManager.put("nimbusSelectionBackground", TABLE_SELECT);
            UIManager.put("text",                   TEXT_PRIMARY);

            UIManager.put("Table.alternateRowColor", TABLE_ROW_ALT);
            UIManager.put("Table.background",        TABLE_ROW);
            UIManager.put("Table.foreground",        TEXT_PRIMARY);
            UIManager.put("Table.gridColor",         BORDER_COLOR);

            UIManager.put("TextField.font",          FONT_BODY);
            UIManager.put("Label.font",              FONT_BODY);
            UIManager.put("Button.font",             FONT_BUTTON);
            UIManager.put("ComboBox.font",           FONT_BODY);
            UIManager.put("Table.font",              FONT_BODY);
            UIManager.put("TableHeader.font",        FONT_BUTTON);
            UIManager.put("TitledBorder.font",       FONT_SUBTITLE);

            UIManager.put("OptionPane.messageFont",  FONT_BODY);
            UIManager.put("OptionPane.buttonFont",   FONT_BUTTON);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Component factory ───────────────────────────────────────────

    /** Creates a styled navigation button for the sidebar. */
    public static JButton createNavButton(String text, String emoji) {
        JButton btn = new JButton(emoji + "  " + text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(BG_SURFACE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setPreferredSize(new Dimension(200, 44));
        btn.setMaximumSize(new Dimension(200, 44));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.isSelected()) btn.setBackground(BG_ELEVATED);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.isSelected()) btn.setBackground(BG_SURFACE);
            }
        });
        return btn;
    }

    /** Marks a nav button as active. */
    public static void setNavActive(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(new Color(30, 60, 110));
            btn.setForeground(ACCENT_BLUE);
        } else {
            btn.setBackground(BG_SURFACE);
            btn.setForeground(TEXT_PRIMARY);
        }
    }

    /** Creates a styled action button. */
    public static JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        Color hoverColor = bgColor.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hoverColor); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bgColor); }
        });
        return btn;
    }

    /** Creates a card panel with dark background and rounded border. */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    /** Creates a section title label. */
    public static JLabel createSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SUBTITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    /** Creates a standard form label. */
    public static JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    /** Creates a styled text field. */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_DARK);
        tf.setCaretColor(ACCENT_BLUE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    /** Creates a styled combo box. */
    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_BODY);
        cb.setForeground(TEXT_PRIMARY);
        cb.setBackground(BG_DARK);
        return cb;
    }

    /** Creates a status bar at the bottom. */
    public static JPanel createStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        bar.setBackground(BG_DARK);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        return bar;
    }

    /** Status bar label. */
    public static JLabel createStatusLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }
}
