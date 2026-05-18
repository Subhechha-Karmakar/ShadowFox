import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;

/**
 * Swing GUI Calculator with dark theme, tabbed interface, and expression parsing.
 *
 * Engineering Concepts Demonstrated:
 *   - Event Handling (ActionListener) on every button
 *   - Layout Management (GridLayout, GridBagLayout, BorderLayout)
 *   - Custom component painting (dark theme, rounded buttons, hover effects)
 *   - Expression parsing with BODMAS/PEMDAS via ExpressionParser
 *   - BigDecimal precision throughout
 */
public class CalculatorGUI extends JFrame {

    // ═══════════════════════════════════════════════════════════════
    //  THEME COLORS
    // ═══════════════════════════════════════════════════════════════
    private static final Color BG_DARK       = new Color(30, 30, 46);
    private static final Color BG_SURFACE    = new Color(42, 42, 60);
    private static final Color BG_BUTTON     = new Color(58, 58, 76);
    private static final Color BG_BUTTON_HOV = new Color(73, 73, 95);
    private static final Color ACCENT_ORANGE = new Color(159, 140, 118);
    private static final Color ACCENT_ORG_HV = new Color(255, 175, 60);
    private static final Color ACCENT_GREEN  = new Color(76, 175, 80);
    private static final Color ACCENT_GRN_HV = new Color(102, 200, 106);
    private static final Color ACCENT_RED    = new Color(123, 63, 0);
    private static final Color ACCENT_RED_HV = new Color(255, 120, 120);
    private static final Color ACCENT_PURPLE = new Color(150, 120, 255);
    private static final Color ACCENT_PUR_HV = new Color(175, 150, 255);
    private static final Color TEXT_PRIMARY   = new Color(205, 214, 244);
    private static final Color TEXT_DIM       = new Color(148, 156, 187);
    private static final Color DISPLAY_BG    = new Color(24, 24, 37);

    private static final Font FONT_DISPLAY   = new Font("Consolas", Font.PLAIN, 28);
    private static final Font FONT_RESULT    = new Font("Consolas", Font.BOLD, 36);
    private static final Font FONT_BUTTON    = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_BUTTON_SM = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_LABEL     = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 14);

    // ═══════════════════════════════════════════════════════════════
    //  BASIC TAB COMPONENTS
    // ═══════════════════════════════════════════════════════════════
    private JTextField expressionField;
    private JLabel resultLabel;

    // ═══════════════════════════════════════════════════════════════
    //  MAIN
    // ═══════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            new CalculatorGUI().setVisible(true);
        });
    }

    public CalculatorGUI() {
        setTitle("Calculator — BigDecimal Precision");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_DARK);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_DARK);
        tabs.setForeground(TEXT_PRIMARY);
        tabs.setFont(FONT_TITLE);
        tabs.setOpaque(true);

        tabs.addTab("⌨ Basic", buildBasicTab());
        tabs.addTab("ƒ Scientific", buildScientificTab());
        tabs.addTab("⇄ Conversions", buildConversionsTab());

        // Style the tab pane
        UIManager.put("TabbedPane.selected", BG_SURFACE);
        UIManager.put("TabbedPane.contentAreaColor", BG_DARK);
        UIManager.put("TabbedPane.background", BG_DARK);
        UIManager.put("TabbedPane.shadow", BG_DARK);
        UIManager.put("TabbedPane.darkShadow", BG_DARK);
        UIManager.put("TabbedPane.focus", BG_DARK);

        add(tabs);
        pack();
        setLocationRelativeTo(null);
    }

    // ═══════════════════════════════════════════════════════════════
    //  BASIC TAB
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildBasicTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Display area ──
        JPanel displayPanel = new JPanel(new BorderLayout(0, 2));
        displayPanel.setBackground(DISPLAY_BG);
        displayPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BG_SURFACE, 2),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        expressionField = new JTextField();
        expressionField.setFont(FONT_DISPLAY);
        expressionField.setForeground(TEXT_DIM);
        expressionField.setBackground(DISPLAY_BG);
        expressionField.setBorder(null);
        expressionField.setCaretColor(ACCENT_ORANGE);
        expressionField.setHorizontalAlignment(SwingConstants.RIGHT);
        expressionField.addActionListener(e -> evaluateExpression());

        resultLabel = new JLabel("0");
        resultLabel.setFont(FONT_RESULT);
        resultLabel.setForeground(TEXT_PRIMARY);
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        displayPanel.add(expressionField, BorderLayout.NORTH);
        displayPanel.add(resultLabel, BorderLayout.SOUTH);

        // ── Button grid ──
        JPanel buttonGrid = new JPanel(new GridLayout(5, 4, 6, 6));
        buttonGrid.setBackground(BG_DARK);

        // Row 1: C  ( )  ⌫
        buttonGrid.add(makeButton("C",  ACCENT_RED,    ACCENT_RED_HV, Color.WHITE, this::onClear));
        buttonGrid.add(makeButton("(",  BG_BUTTON,     BG_BUTTON_HOV, ACCENT_PURPLE, this::onAppend));
        buttonGrid.add(makeButton(")",  BG_BUTTON,     BG_BUTTON_HOV, ACCENT_PURPLE, this::onAppend));
        buttonGrid.add(makeButton("⌫", ACCENT_RED,    ACCENT_RED_HV, Color.WHITE, this::onBackspace));
        // Row 2: 7  8  9  ÷
        buttonGrid.add(makeButton("7",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("8",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("9",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("÷",  ACCENT_ORANGE, ACCENT_ORG_HV, Color.WHITE, this::onAppend));
        // Row 3: 4  5  6  ×
        buttonGrid.add(makeButton("4",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("5",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("6",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("×",  ACCENT_ORANGE, ACCENT_ORG_HV, Color.WHITE, this::onAppend));
        // Row 4: 1  2  3  -
        buttonGrid.add(makeButton("1",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("2",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("3",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("-",  ACCENT_ORANGE, ACCENT_ORG_HV, Color.WHITE, this::onAppend));
        // Row 5: 0  .  =  +
        buttonGrid.add(makeButton("0",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton(".",  BG_BUTTON,     BG_BUTTON_HOV, TEXT_PRIMARY, this::onAppend));
        buttonGrid.add(makeButton("=",  ACCENT_GREEN,  ACCENT_GRN_HV, Color.WHITE, this::onEquals));
        buttonGrid.add(makeButton("+",  ACCENT_ORANGE, ACCENT_ORG_HV, Color.WHITE, this::onAppend));

        panel.add(displayPanel, BorderLayout.NORTH);
        panel.add(buttonGrid, BorderLayout.CENTER);

        // ── Hint label ──
        JLabel hint = new JLabel("Type expressions like  5 + 3 * 2  or  sqrt(16)  and press Enter / =");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(TEXT_DIM);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        panel.add(hint, BorderLayout.SOUTH);

        panel.setPreferredSize(new Dimension(380, 520));
        return panel;
    }

    // ── Basic tab actions ──
    private void onAppend(ActionEvent e) {
        String text = ((JButton) e.getSource()).getText();
        expressionField.setText(expressionField.getText() + text);
        expressionField.requestFocus();
    }

    private void onClear(ActionEvent e) {
        expressionField.setText("");
        resultLabel.setText("0");
        resultLabel.setForeground(TEXT_PRIMARY);
        expressionField.requestFocus();
    }

    private void onBackspace(ActionEvent e) {
        String text = expressionField.getText();
        if (!text.isEmpty()) {
            expressionField.setText(text.substring(0, text.length() - 1));
        }
        expressionField.requestFocus();
    }

    private void onEquals(ActionEvent e) {
        evaluateExpression();
    }

    private void evaluateExpression() {
        String expr = expressionField.getText().trim();
        if (expr.isEmpty()) return;
        try {
            ExpressionParser parser = new ExpressionParser(expr);
            BigDecimal result = parser.parse();
            String formatted = result.stripTrailingZeros().toPlainString();
            resultLabel.setText(formatted);
            resultLabel.setForeground(TEXT_PRIMARY);
        } catch (ArithmeticException ex) {
            resultLabel.setText("Math Error");
            resultLabel.setForeground(ACCENT_RED);
        } catch (RuntimeException ex) {
            resultLabel.setText("Syntax Error");
            resultLabel.setForeground(ACCENT_RED);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  SCIENTIFIC TAB
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildScientificTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        JLabel title = new JLabel("Scientific Calculations");
        title.setFont(FONT_TITLE);
        title.setForeground(ACCENT_PURPLE);
        panel.add(title, gbc);

        // ── Square Root Section ──
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(makeLabel("Number:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        JTextField sqrtInput = makeTextField();
        panel.add(sqrtInput, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel sqrtResult = makeResultLabel();
        JButton sqrtBtn = makeButton("√  Square Root", ACCENT_PURPLE, ACCENT_PUR_HV, Color.WHITE, e -> {
            computeScientific(sqrtInput, sqrtResult, "sqrt");
        });
        sqrtBtn.setFont(FONT_BUTTON_SM);
        panel.add(sqrtBtn, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        panel.add(sqrtResult, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        JSeparator sep = new JSeparator();
        sep.setForeground(BG_SURFACE);
        panel.add(sep, gbc);

        // ── Exponentiation Section ──
        gbc.gridy = 4; gbc.gridwidth = 1; gbc.weightx = 0;
        panel.add(makeLabel("Base:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        JTextField powBase = makeTextField();
        panel.add(powBase, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weightx = 0;
        panel.add(makeLabel("Exponent:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        JTextField powExp = makeTextField();
        panel.add(powExp, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel powResult = makeResultLabel();
        JButton powBtn = makeButton("xⁿ  Power", ACCENT_PURPLE, ACCENT_PUR_HV, Color.WHITE, e -> {
            computePower(powBase, powExp, powResult);
        });
        powBtn.setFont(FONT_BUTTON_SM);
        panel.add(powBtn, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        panel.add(powResult, gbc);

        // Spacer
        gbc.gridx = 0; gbc.gridy = 7; gbc.weighty = 1; gbc.gridwidth = 3;
        panel.add(Box.createVerticalGlue(), gbc);

        panel.setPreferredSize(new Dimension(380, 520));
        return panel;
    }

    private void computeScientific(JTextField input, JLabel result, String op) {
        try {
            BigDecimal val = new BigDecimal(input.getText().trim());
            BigDecimal res = Scientific.squareRoot(val);
            result.setText("= " + res.stripTrailingZeros().toPlainString());
            result.setForeground(ACCENT_GREEN);
        } catch (NumberFormatException ex) {
            result.setText("Invalid number");
            result.setForeground(ACCENT_RED);
        } catch (ArithmeticException ex) {
            result.setText(ex.getMessage());
            result.setForeground(ACCENT_RED);
        }
    }

    private void computePower(JTextField baseField, JTextField expField, JLabel result) {
        try {
            BigDecimal base = new BigDecimal(baseField.getText().trim());
            BigDecimal exp = new BigDecimal(expField.getText().trim());
            BigDecimal res = Scientific.exponentiation(base, exp);
            result.setText("= " + res.stripTrailingZeros().toPlainString());
            result.setForeground(ACCENT_GREEN);
        } catch (NumberFormatException ex) {
            result.setText("Invalid number");
            result.setForeground(ACCENT_RED);
        } catch (ArithmeticException ex) {
            result.setText(ex.getMessage());
            result.setForeground(ACCENT_RED);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  CONVERSIONS TAB
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildConversionsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ── Temperature Section ──
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        JLabel tempTitle = new JLabel("🌡 Temperature");
        tempTitle.setFont(FONT_TITLE);
        tempTitle.setForeground(ACCENT_ORANGE);
        panel.add(tempTitle, gbc);

        String[] tempOptions = {"Celsius → Fahrenheit", "Fahrenheit → Celsius",
                                "Celsius → Kelvin", "Kelvin → Celsius"};
        JComboBox<String> tempCombo = new JComboBox<>(tempOptions);
        styleCombo(tempCombo);
        gbc.gridy = 1; gbc.gridwidth = 3;
        panel.add(tempCombo, gbc);

        gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0;
        panel.add(makeLabel("Value:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        JTextField tempInput = makeTextField();
        panel.add(tempInput, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel tempResult = makeResultLabel();
        JButton tempBtn = makeButton("Convert", ACCENT_ORANGE, ACCENT_ORG_HV, Color.WHITE, e -> {
            convertTemperature(tempCombo, tempInput, tempResult);
        });
        tempBtn.setFont(FONT_BUTTON_SM);
        panel.add(tempBtn, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        panel.add(tempResult, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        JSeparator sep = new JSeparator();
        sep.setForeground(BG_SURFACE);
        panel.add(sep, gbc);

        // ── Currency Section ──
        gbc.gridy = 5; gbc.gridwidth = 3;
        JLabel currTitle = new JLabel("💱 Currency");
        currTitle.setFont(FONT_TITLE);
        currTitle.setForeground(ACCENT_GREEN);
        panel.add(currTitle, gbc);

        String[] currencies = {"USD", "EUR", "GBP", "INR"};
        gbc.gridy = 6; gbc.gridwidth = 1; gbc.weightx = 0;
        panel.add(makeLabel("From:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;
        JComboBox<String> fromCurr = new JComboBox<>(currencies);
        styleCombo(fromCurr);
        panel.add(fromCurr, gbc);
        gbc.gridx = 2;
        panel.add(makeLabel("  To:"), gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 1; gbc.weightx = 0;
        JComboBox<String> toCurr = new JComboBox<>(currencies);
        toCurr.setSelectedIndex(1);
        styleCombo(toCurr);
        panel.add(toCurr, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        JTextField currInput = makeTextField();
        panel.add(currInput, gbc);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel currResult = makeResultLabel();
        JButton currBtn = makeButton("Convert", ACCENT_GREEN, ACCENT_GRN_HV, Color.WHITE, e -> {
            convertCurrency(fromCurr, toCurr, currInput, currResult);
        });
        currBtn.setFont(FONT_BUTTON_SM);
        panel.add(currBtn, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1;
        panel.add(currResult, gbc);

        // Spacer
        gbc.gridx = 0; gbc.gridy = 9; gbc.weighty = 1; gbc.gridwidth = 3;
        panel.add(Box.createVerticalGlue(), gbc);

        panel.setPreferredSize(new Dimension(380, 520));
        return panel;
    }

    private void convertTemperature(JComboBox<String> combo, JTextField input, JLabel result) {
        try {
            BigDecimal val = new BigDecimal(input.getText().trim());
            BigDecimal res;
            String unit;
            switch (combo.getSelectedIndex()) {
                case 0: res = Conversions.celsiusToFahrenheit(val); unit = "°F"; break;
                case 1: res = Conversions.fahrenheitToCelsius(val); unit = "°C"; break;
                case 2: res = Conversions.celsiusToKelvin(val);     unit = "K";  break;
                case 3: res = Conversions.kelvinToCelsius(val);     unit = "°C"; break;
                default: return;
            }
            result.setText("= " + res.stripTrailingZeros().toPlainString() + " " + unit);
            result.setForeground(ACCENT_GREEN);
        } catch (NumberFormatException ex) {
            result.setText("Invalid number");
            result.setForeground(ACCENT_RED);
        }
    }

    private void convertCurrency(JComboBox<String> from, JComboBox<String> to,
                                  JTextField input, JLabel result) {
        try {
            BigDecimal val = new BigDecimal(input.getText().trim());
            String fromCode = (String) from.getSelectedItem();
            String toCode = (String) to.getSelectedItem();
            BigDecimal res = Conversions.convertCurrency(val, fromCode, toCode);
            result.setText("= " + res.stripTrailingZeros().toPlainString() + " " + toCode);
            result.setForeground(ACCENT_GREEN);
        } catch (NumberFormatException ex) {
            result.setText("Invalid number");
            result.setForeground(ACCENT_RED);
        } catch (IllegalArgumentException ex) {
            result.setText(ex.getMessage());
            result.setForeground(ACCENT_RED);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  UI FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════

    /** Creates a styled rounded button with hover animation. */
    private JButton makeButton(String text, Color bg, Color hover, Color fg, ActionListener action) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? hover : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(80, 58));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        return btn;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_DIM);
        return lbl;
    }

    private JLabel makeResultLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(new Font("Consolas", Font.BOLD, 16));
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    private JTextField makeTextField() {
        JTextField tf = new JTextField(12);
        tf.setFont(FONT_LABEL);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_SURFACE);
        tf.setCaretColor(ACCENT_ORANGE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BG_BUTTON, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return tf;
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setFont(FONT_LABEL);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(BG_SURFACE);
    }
}
