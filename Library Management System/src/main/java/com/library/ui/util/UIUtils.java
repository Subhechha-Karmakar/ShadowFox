package com.library.ui.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Shared UI utilities and design tokens for the Library Management System.
 * Provides consistent colors, fonts, and helper factory methods.
 */
public final class UIUtils {

    // ── Design tokens ─────────────────────────────────────────────────────────
    public static final Color BG_DARK      = new Color(15,  17,  26);   // deepest bg
    public static final Color BG_PANEL     = new Color(30,  41,  59);   // slate-800  (panel bg)
    public static final Color BG_CARD      = new Color(44,  58,  82);   // slate-700  (card / table bg)
    public static final Color ACCENT       = new Color(99, 102, 241);   // indigo-500
    public static final Color ACCENT_HOVER = new Color(79,  70, 229);   // indigo-600
    public static final Color SUCCESS      = new Color(52, 211, 153);   // emerald-400
    public static final Color DANGER       = new Color(248, 113, 113);  // red-400
    public static final Color WARNING      = new Color(251, 191,  36);  // amber-400
    public static final Color TEXT_PRIMARY = new Color(226, 232, 240);  // slate-200  (light)
    public static final Color TEXT_MUTED   = new Color(148, 163, 184);  // slate-400  (muted light)
    public static final Color BORDER       = new Color(71,  85, 105);   // slate-600

    public static final Font FONT_HEADING  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO     = new Font("Consolas", Font.PLAIN, 12);

    private UIUtils() {}

    // ── Global look-and-feel ──────────────────────────────────────────────────
    public static void applyDarkTheme() {
        UIManager.put("Panel.background",            BG_DARK);
        UIManager.put("OptionPane.background",       BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Label.foreground",            TEXT_PRIMARY);
        UIManager.put("Label.font",                  FONT_BODY);
        UIManager.put("Button.background",           ACCENT);
        UIManager.put("Button.foreground",           Color.WHITE);
        UIManager.put("Button.font",                 FONT_TITLE);
        UIManager.put("TextField.background",        BG_CARD);
        UIManager.put("TextField.foreground",        TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",   TEXT_PRIMARY);
        UIManager.put("TextField.font",              FONT_BODY);
        UIManager.put("PasswordField.background",    BG_CARD);
        UIManager.put("PasswordField.foreground",    TEXT_PRIMARY);
        UIManager.put("PasswordField.caretForeground", TEXT_PRIMARY);
        UIManager.put("PasswordField.font",          FONT_BODY);
        UIManager.put("ComboBox.background",         BG_CARD);
        UIManager.put("ComboBox.foreground",         TEXT_PRIMARY);
        UIManager.put("ComboBox.font",               FONT_BODY);
        UIManager.put("Table.background",            BG_CARD);
        UIManager.put("Table.foreground",            TEXT_PRIMARY);
        UIManager.put("Table.font",                  FONT_BODY);
        UIManager.put("Table.selectionBackground",   ACCENT);
        UIManager.put("Table.selectionForeground",   Color.WHITE);
        UIManager.put("Table.gridColor",             BORDER);
        UIManager.put("TableHeader.background",      new Color(203, 213, 225));  // slate-300
        UIManager.put("TableHeader.foreground",      new Color(15, 23, 42));     // near-black
        UIManager.put("TableHeader.font",            FONT_TITLE);
        UIManager.put("ScrollPane.background",       BG_DARK);
        UIManager.put("ScrollBar.background",        BG_PANEL);
        UIManager.put("ScrollBar.thumb",             BORDER);
        UIManager.put("TabbedPane.background",       BG_DARK);
        UIManager.put("TabbedPane.foreground",       new Color(15, 23, 42));   // near-black
        UIManager.put("TabbedPane.selected",         BG_PANEL);
        UIManager.put("TabbedPane.font",             FONT_TITLE);
        UIManager.put("Dialog.background",           BG_PANEL);
    }

    // ── Factory: accent button ─────────────────────────────────────────────────
    public static JButton accentButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_TITLE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(ACCENT_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(ACCENT); }
        });
        return btn;
    }

    // ── Factory: danger button ────────────────────────────────────────────────
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_TITLE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.addMouseListener(new MouseAdapter() {
            final Color hover = new Color(239, 68, 68);
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(DANGER); }
        });
        return btn;
    }

    // ── Factory: success button ────────────────────────────────────────────────
    public static JButton successButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(SUCCESS);
        btn.setForeground(new Color(5, 46, 22));
        btn.setFont(FONT_TITLE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    // ── Factory: styled text field ────────────────────────────────────────────
    public static JTextField styledField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setBackground(BG_CARD);
        tf.setForeground(TEXT_PRIMARY);
        tf.setFont(FONT_BODY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    // ── Factory: styled password field ────────────────────────────────────────
    public static JPasswordField styledPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setBackground(BG_CARD);
        pf.setForeground(TEXT_PRIMARY);
        pf.setFont(FONT_BODY);
        pf.setCaretColor(TEXT_PRIMARY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    // ── Factory: section header label (dark font) ──────────────────────────────
    public static JLabel headerLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING);
        label.setForeground(new Color(15, 23, 42));   // slate-900 (near-black)
        label.setBorder(new EmptyBorder(8, 0, 12, 0));
        return label;
    }

    // ── Factory: muted label ──────────────────────────────────────────────────
    public static JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SMALL);
        label.setForeground(TEXT_MUTED);
        return label;
    }

    // ── Style a JTable for dark theme ─────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setGridColor(BORDER);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Column header: light bg + dark text for clear readability
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(203, 213, 225));   // slate-300 (light)
        header.setForeground(new Color(15,  23,  42));    // near-black
        header.setFont(FONT_TITLE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // ── Styled scroll pane ────────────────────────────────────────────────────
    public static JScrollPane darkScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        return sp;
    }

    // ── Error dialog ──────────────────────────────────────────────────────────
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── Error dialog with dark-styled OK button ───────────────────────────────
    public static void showDarkError(Component parent, String message) {
        Color darkBg    = new Color(30,  41,  59);   // slate-800
        Color darkHover = new Color(15,  23,  42);   // slate-900

        // Use a JLabel so we can control the message text color independently
        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(FONT_BODY);
        msgLabel.setForeground(new Color(226, 232, 240));   // slate-200 (light)

        JButton okBtn = new JButton("OK");
        okBtn.setBackground(darkBg);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFont(FONT_TITLE);
        okBtn.setFocusPainted(false);
        okBtn.setBorderPainted(false);
        okBtn.setOpaque(true);
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okBtn.setBorder(new EmptyBorder(8, 28, 8, 28));
        okBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { okBtn.setBackground(darkHover); }
            @Override public void mouseExited (MouseEvent e) { okBtn.setBackground(darkBg);    }
        });

        JOptionPane pane = new JOptionPane(msgLabel,
            JOptionPane.ERROR_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{ okBtn },
            okBtn);

        JDialog dlg = pane.createDialog(parent, "Error");
        okBtn.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    // ── Info dialog ───────────────────────────────────────────────────────────
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Dark-styled Yes / No confirm dialog ───────────────────────────────────
    /**
     * Shows a YES_NO confirmation dialog whose buttons are dark-colored.
     *
     * @return {@code JOptionPane.YES_OPTION} if the user clicks Yes, otherwise
     *         {@code JOptionPane.NO_OPTION} / {@code JOptionPane.CLOSED_OPTION}.
     */
    public static int showDarkConfirm(Component parent, String message, String title) {
        // Build dark-styled buttons explicitly so they override the global theme
        JButton yesBtn = new JButton("Yes");
        JButton noBtn  = new JButton("No");

        Color darkBg   = new Color(30,  41,  59);   // slate-800
        Color darkHover= new Color(15,  23,  42);   // slate-900

        for (JButton btn : new JButton[]{ yesBtn, noBtn }) {
            btn.setBackground(darkBg);
            btn.setForeground(Color.WHITE);
            btn.setFont(FONT_TITLE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(8, 22, 8, 22));
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { btn.setBackground(darkHover); }
                @Override public void mouseExited (MouseEvent e) { btn.setBackground(darkBg);    }
            });
        }

        JOptionPane pane = new JOptionPane(message,
            JOptionPane.QUESTION_MESSAGE,
            JOptionPane.YES_NO_OPTION,
            null,
            new Object[]{ yesBtn, noBtn },
            yesBtn);

        JDialog dlg = pane.createDialog(parent, title);

        // Wire buttons after dialog is created
        yesBtn.addActionListener(e -> { pane.setValue(JOptionPane.YES_OPTION); dlg.dispose(); });
        noBtn .addActionListener(e -> { pane.setValue(JOptionPane.NO_OPTION);  dlg.dispose(); });

        dlg.setVisible(true);

        Object val = pane.getValue();
        if (val instanceof Integer) return (Integer) val;
        return JOptionPane.CLOSED_OPTION;
    }
}
