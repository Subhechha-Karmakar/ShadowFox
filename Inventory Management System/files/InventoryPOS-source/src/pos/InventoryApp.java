package pos;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class InventoryApp extends JFrame {

    // ─── Singleton manager ────────────────────────────────────────────────────
    private final BillManager mgr = BillManager.getInstance();

    // ─── Colors matching screenshot exactly ───────────────────────────────────
    private static final Color DARK_BLUE    = new Color(0,   0,   80);
    private static final Color HEADER_BG    = new Color(0,   0,  100);
    private static final Color ORANGE       = new Color(220, 100,  0);
    private static final Color COSMETIC_HDR = new Color(230, 100, 50);   // orange-red
    private static final Color COSMETIC_ROW = new Color(230, 120, 60);
    private static final Color GROCERY_HDR  = new Color(200, 200,  0);   // yellow
    private static final Color GROCERY_ROW  = new Color(210, 210,  0);
    private static final Color DRINK_HDR    = new Color(60,  180, 180);  // teal
    private static final Color DRINK_ROW    = new Color(70,  190, 190);
    private static final Color BILLING_HDR  = new Color(60,  160, 190);
    private static final Color INFO_BG      = new Color(200, 230, 255);
    private static final Color BILL_MENU_BG = new Color(180, 200, 230);
    private static final Color ROW_BG       = new Color(240, 248, 255);
    private static final Color BTN_TOTAL    = new Color(220,  80,  20);
    private static final Color BTN_GENERATE = new Color(0,   120, 200);
    private static final Color BTN_CLEAR    = new Color(220,  30,  30);
    private static final Color BTN_EXIT     = new Color(30,  160,  30);
    private static final Color BTN_LOGOUT   = new Color(220,  60,   0);
    private static final Color BTN_SEARCH   = new Color(60,  100, 180);

    // ─── Product quantity spinners (name → spinner) ───────────────────────────
    private final Map<String, JSpinner> spinners = new LinkedHashMap<>();

    // ─── Summary fields ───────────────────────────────────────────────────────
    private JTextField fTotalCosmetic, fTotalGrocery, fTotalColdDrink;
    private JTextField fCosmeticTax,   fGroceryTax,   fColdDrinkTax;

    // ─── Customer fields ──────────────────────────────────────────────────────
    private JTextField fCustomerName, fPhone, fBillNumber;

    // ─── Billing text area ────────────────────────────────────────────────────
    private JTextArea  billingArea;

    // ─────────────────────────────────────────────────────────────────────────
    public InventoryApp() {
        super("Inventory Management System | Project Zone");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        setBackground(DARK_BLUE);

        add(buildTitleBar(),     BorderLayout.NORTH);
        add(buildCenterPanel(),  BorderLayout.CENTER);

        pack();
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TITLE BAR
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(HEADER_BG);
        bar.setBorder(new EmptyBorder(8, 14, 8, 14));

        JLabel title = new JLabel("  Inventory Management System | Project Zone");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JButton logout = new JButton("Logout");
        logout.setBackground(BTN_LOGOUT);
        logout.setForeground(Color.WHITE);
        logout.setFont(new Font("Arial", Font.BOLD, 13));
        logout.setFocusPainted(false);
        logout.setBorder(new EmptyBorder(6, 16, 6, 16));
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> System.exit(0));

        bar.add(title,  BorderLayout.WEST);
        bar.add(logout, BorderLayout.EAST);
        return bar;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CENTER PANEL (everything below title bar)
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildCenterPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 4));
        outer.setBackground(DARK_BLUE);
        outer.setBorder(new EmptyBorder(4, 6, 4, 6));

        outer.add(buildCustomerBar(), BorderLayout.NORTH);
        outer.add(buildProductArea(), BorderLayout.CENTER);
        outer.add(buildBillMenu(),    BorderLayout.SOUTH);
        return outer;
    }

    // ─── Customer bar ──────────────────────────────────────────────────────────
    private JPanel buildCustomerBar() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(220, 230, 255));
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1),
                new EmptyBorder(3, 6, 3, 6)));

        JLabel hint = new JLabel("Customer Details | Name and Phone No. should be Required to Generate the Total Bill.");
        hint.setFont(new Font("Arial", Font.PLAIN, 11));
        hint.setForeground(new Color(40, 40, 100));

        JPanel fields = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        fields.setBackground(new Color(220, 230, 255));

        fCustomerName = inputField(14);
        fPhone        = inputField(13);
        fBillNumber   = inputField(10);

        JButton search = colorBtn("Search", BTN_SEARCH);
        search.addActionListener(e -> searchBill());

        fields.add(boldLabel("Customer Name"));  fields.add(fCustomerName);
        fields.add(boldLabel("Phone No."));      fields.add(fPhone);
        fields.add(boldLabel("Bill Number"));    fields.add(fBillNumber);
        fields.add(search);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(220, 230, 255));
        top.add(hint,   BorderLayout.NORTH);
        top.add(fields, BorderLayout.CENTER);
        wrapper.add(top);
        return wrapper;
    }

    // ─── Product area (4 columns) ─────────────────────────────────────────────
    private JPanel buildProductArea() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 4, 0));
        grid.setBackground(DARK_BLUE);

        grid.add(buildCategoryPanel("Cosmetics",   COSMETIC_HDR, COSMETIC_ROW,
                BillManager.COSMETICS));
        grid.add(buildCategoryPanel("Grocery",     GROCERY_HDR,  GROCERY_ROW,
                BillManager.GROCERY));
        grid.add(buildCategoryPanel("Cold Drinks", DRINK_HDR,    DRINK_ROW,
                BillManager.COLD_DRINKS));
        grid.add(buildBillingPanel());
        return grid;
    }

    private JPanel buildCategoryPanel(String title, Color headerColor,
                                      Color rowColor, List<String> products) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(rowColor);
        panel.setBorder(new LineBorder(Color.GRAY, 1));

        // Header
        JLabel hdr = new JLabel(title, SwingConstants.CENTER);
        hdr.setFont(new Font("Arial", Font.BOLD, 13));
        hdr.setForeground(Color.WHITE);
        hdr.setBackground(headerColor);
        hdr.setOpaque(true);
        hdr.setBorder(new EmptyBorder(5, 0, 5, 0));
        panel.add(hdr, BorderLayout.NORTH);

        // Rows
        JPanel rows = new JPanel(new GridLayout(products.size(), 1, 0, 0));
        rows.setBackground(rowColor);
        for (String name : products) {
            rows.add(buildProductRow(name, rowColor));
        }
        
        JPanel rowsWrapper = new JPanel(new BorderLayout());
        rowsWrapper.setBackground(rowColor);
        rowsWrapper.add(rows, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(rowsWrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildProductRow(String name, Color bg) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(bg);
        row.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel lbl = new JLabel(name);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(Color.BLACK);
        lbl.setPreferredSize(new Dimension(135, 22));

        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 999, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font("Arial", Font.PLAIN, 12));
        spinner.setPreferredSize(new Dimension(55, 24));
        // Live update totals on change
        spinner.addChangeListener(e -> recalculate());
        spinners.put(name, spinner);

        row.add(lbl,     BorderLayout.WEST);
        row.add(spinner, BorderLayout.EAST);
        return row;
    }

    // ─── Customer Billing Area (right column) ─────────────────────────────────
    private JPanel buildBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBorder(new LineBorder(Color.GRAY, 1));

        JLabel hdr = new JLabel("Customer Billing Area", SwingConstants.CENTER);
        hdr.setFont(new Font("Arial", Font.BOLD, 13));
        hdr.setForeground(Color.WHITE);
        hdr.setBackground(BILLING_HDR);
        hdr.setOpaque(true);
        hdr.setBorder(new EmptyBorder(5, 0, 5, 0));
        panel.add(hdr, BorderLayout.NORTH);

        billingArea = new JTextArea();
        billingArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        billingArea.setEditable(false);
        billingArea.setBackground(new Color(245, 252, 255));
        billingArea.setBorder(new EmptyBorder(4, 6, 4, 6));
        billingArea.setText("Bill details will appear here\nafter clicking 'Generate Bill'.");

        JScrollPane scroll = new JScrollPane(billingArea);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ─── Bill Menu (bottom bar) ───────────────────────────────────────────────
    private JPanel buildBillMenu() {
        JPanel outer = new JPanel(new BorderLayout(0, 0));
        outer.setBackground(DARK_BLUE);
        outer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Bill Menu", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 11), Color.WHITE));

        JPanel fields = new JPanel(new GridLayout(3, 2, 6, 4));
        fields.setBackground(BILL_MENU_BG);
        fields.setBorder(new EmptyBorder(6, 8, 6, 8));

        fTotalCosmetic  = readonlyField(); fCosmeticTax  = readonlyField();
        fTotalGrocery   = readonlyField(); fGroceryTax   = readonlyField();
        fTotalColdDrink = readonlyField(); fColdDrinkTax = readonlyField();

        fields.add(labeledField("Total Cosmetic Price",      fTotalCosmetic));
        fields.add(labeledField("Cosmetic Tax",              fCosmeticTax));
        fields.add(labeledField("Total Grocery Price",       fTotalGrocery));
        fields.add(labeledField("Grocery Tax",               fGroceryTax));
        fields.add(labeledField("Total Cold Drinks Price",   fTotalColdDrink));
        fields.add(labeledField("Cold Drinks Tax",           fColdDrinkTax));

        // Buttons
        JPanel btns = new JPanel(new GridLayout(1, 4, 6, 0));
        btns.setBackground(BILL_MENU_BG);
        btns.setBorder(new EmptyBorder(6, 8, 6, 8));

        JButton btnTotal    = colorBtn("Total",         BTN_TOTAL);
        JButton btnGenerate = colorBtn("Generate Bill", BTN_GENERATE);
        JButton btnClear    = colorBtn("Clear",         BTN_CLEAR);
        JButton btnExit     = colorBtn("Exit",          BTN_EXIT);

        btnTotal.addActionListener(e    -> recalculate());
        btnGenerate.addActionListener(e -> generateBill());
        btnClear.addActionListener(e    -> clearAll());
        btnExit.addActionListener(e     -> System.exit(0));

        btns.add(btnTotal);
        btns.add(btnGenerate);
        btns.add(btnClear);
        btns.add(btnExit);

        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setBackground(BILL_MENU_BG);
        bottom.add(fields, BorderLayout.CENTER);
        bottom.add(btns,   BorderLayout.EAST);

        outer.add(bottom);
        return outer;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  BUSINESS LOGIC
    // ═════════════════════════════════════════════════════════════════════════
    private void recalculate() {
        double cosTotal = categoryTotal(BillManager.COSMETICS);
        double groTotal = categoryTotal(BillManager.GROCERY);
        double cdTotal  = categoryTotal(BillManager.COLD_DRINKS);

        double cosTax = cosTotal * BillManager.COSMETIC_TAX_RATE;
        double groTax = groTotal * BillManager.GROCERY_TAX_RATE;
        double cdTax  = cdTotal  * BillManager.COLD_DRINK_TAX_RATE;

        fTotalCosmetic.setText(String.format("Rs. %.1f",  cosTotal));
        fTotalGrocery.setText(String.format("Rs. %.1f",   groTotal));
        fTotalColdDrink.setText(String.format("Rs. %.1f", cdTotal));
        fCosmeticTax.setText(String.format("Rs. %.1f",    cosTax));
        fGroceryTax.setText(String.format("Rs. %.1f",     groTax));
        fColdDrinkTax.setText(String.format("Rs. %.1f",   cdTax));
    }

    private double categoryTotal(List<String> names) {
        return names.stream()
                .mapToDouble(name -> {
                    JSpinner s = spinners.get(name);
                    int qty = s == null ? 0 : (int) s.getValue();
                    return qty * BillManager.PRICES.getOrDefault(name, 0.0);
                }).sum();
    }

    private void generateBill() {
        String name  = fCustomerName.getText().trim();
        String phone = fPhone.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Customer Name and Phone Number are required to generate a bill.",
                    "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }

        recalculate();

        // Build product list
        List<Product> products = new ArrayList<>();
        for (String pName : BillManager.PRICES.keySet()) {
            JSpinner sp = spinners.get(pName);
            int qty = sp == null ? 0 : (int) sp.getValue();
            if (qty > 0) {
                Product p = new Product(pName, BillManager.PRICES.get(pName));
                p.setQuantity(qty);
                products.add(p);
            }
        }

        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add at least one product before generating a bill.",
                    "Empty Bill", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Bill bill = mgr.generateBill(name, phone, products);
        fBillNumber.setText(String.valueOf(bill.getBillNumber()));
        displayBill(bill);
    }

    private void displayBill(Bill bill) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Bill Number : %d%n", bill.getBillNumber()));
        sb.append(String.format("Customer Name : %s%n", bill.getCustomerName()));
        sb.append(String.format("Phone Number : %s%n",  bill.getPhoneNumber()));
        sb.append("=".repeat(38)).append("\n");
        sb.append(String.format("%-16s %4s  %8s%n", "Products", "QTY", "Price"));
        sb.append("=".repeat(38)).append("\n");

        for (Product p : bill.getItems()) {
            sb.append(String.format("%-16s %4d  %8.0f%n",
                    p.getName(), p.getQuantity(), p.getTotal()));
        }

        sb.append("-".repeat(38)).append("\n");
        sb.append(String.format("%-22s Rs. %.1f%n",  "Cosmetic Tax",    bill.getCosmeticTax()));
        sb.append(String.format("%-22s Rs. %.1f%n",  "Grocery Tax",     bill.getGroceryTax()));
        sb.append(String.format("%-22s Rs. %.1f%n",  "Cold Drink Tax",  bill.getColdDrinkTax()));
        sb.append("-".repeat(38)).append("\n");
        sb.append(String.format("%-22s Rs. %.1f%n",  "Total Bill :",    bill.getGrandTotal()));

        billingArea.setText(sb.toString());
        billingArea.setCaretPosition(0);
    }

    private void searchBill() {
        String txt = fBillNumber.getText().trim();
        if (txt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Bill Number to search.",
                    "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            int num = Integer.parseInt(txt);
            mgr.findBill(num).ifPresentOrElse(
                    this::displayBill,
                    () -> JOptionPane.showMessageDialog(this,
                            "No bill found with number " + num,
                            "Not Found", JOptionPane.INFORMATION_MESSAGE));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bill Number must be numeric.",
                    "Invalid", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAll() {
        spinners.values().forEach(s -> s.setValue(0));
        fCustomerName.setText("");
        fPhone.setText("");
        fBillNumber.setText("");
        fTotalCosmetic.setText("Rs. 0.0");
        fTotalGrocery.setText("Rs. 0.0");
        fTotalColdDrink.setText("Rs. 0.0");
        fCosmeticTax.setText("Rs. 0.0");
        fGroceryTax.setText("Rs. 0.0");
        fColdDrinkTax.setText("Rs. 0.0");
        billingArea.setText("Bill details will appear here\nafter clicking 'Generate Bill'.");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═════════════════════════════════════════════════════════════════════════
    private JTextField inputField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        return f;
    }

    private JTextField readonlyField() {
        JTextField f = new JTextField("Rs. 0.0", 8);
        f.setEditable(false);
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        f.setBackground(new Color(230, 240, 255));
        f.setBorder(new LineBorder(Color.GRAY, 1));
        return f;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(BILL_MENU_BG);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(Color.BLACK);
        p.add(l);
        p.add(field);
        return p;
    }

    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(Color.BLACK);
        return l;
    }

    private JButton colorBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        b.setOpaque(true);
        return b;
    }

    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new InventoryApp();
        });
    }
}
