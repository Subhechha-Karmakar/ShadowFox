package pos;

import java.util.*;

/**
 * Singleton: one BillManager per application run.
 * Holds all generated bills and the bill-number counter.
 */
public class BillManager {

    // ── Category membership lists (used by Bill for totals) ──────────────────
    public static final List<String> COSMETICS   = List.of(
            "Gel Face Wash", "Foam Face Wash", "Body Wash", "Bathing Soap",
            "Body Lotion", "Face Cream", "Sunscreen (SPF 30)", "Sunscreen (SPF 50)", "Petroleum Jelly",
            "Shaving Cream", "Razor", "Deodorant", "Body Mist",
            "Anti-dandruff Shampoo", "Color-protect Shampoo", "Hydrating Shampoo", "Hair Conditioner",
            "Coconut Hair Oil", "Almond Hair Oil", "Argan Hair Oil", "Hair Serum", "Hair Styling Gel",
            "Foundation", "BB/CC Cream", "Compact Powder", "Concealer", "Blush",
            "Kajal/Kohl Pencil", "Liquid Eyeliner", "Gel Eyeliner", "Mascara", "Eyeshadow Palette",
            "Matte Lipstick", "Liquid Lip Color", "Lip Gloss", "Tinted Lip Balm",
            "Nail Polish", "Nail Polish Remover"
    );
    public static final List<String> GROCERY     = List.of(
            "Atta (1 kg)", "Maida (1 kg)", "Sooji (500 g)", "Besan (500 g)",
            "Basmati Rice (1 kg)", "Jasmine Rice (1 kg)", "Brown Rice (1 kg)",
            "Oats (500 g)", "Poha (500 g)",
            "Toor dal (1 kg)", "Moong dal (1 kg)", "Masoor dal (1 kg)", "Chana dal (1 kg)",
            "Chickpeas (1 kg)", "Kidney beans (1 kg)",
            "Mustard oil (1 L)", "Sunflower oil (1 L)", "Olive oil (1 L)", "Rice bran oil (1 L)", "Desi Ghee (1 kg)",
            "Turmeric (250 g)", "Chilli powder (250 g)", "Coriander (250 g)", "Garam Masala (250 g)", "Cumin (250 g)", "Iodized Salt (1 kg)",
            "White sugar (1 kg)", "Brown sugar (1 kg)", "Jaggery (1 kg)", "Honey (500 g)",
            "Maggi (100 g)", "Yippee (100 g)", "Macaroni (500 g)", "Spaghetti (500 g)", "Vermicelli (500 g)",
            "Cornflakes (500 g)", "Muesli (500 g)", "Granola (500 g)",
            "Potato chips (100 g)", "Namkeen (200 g)", "Cookies (200 g)", "Marie biscuits (200 g)", "Crackers (200 g)",
            "Tomato ketchup (500 g)", "Mayonnaise (500 g)", "Schezwan chutney (250 g)", "Soy sauce (250 g)", "Peanut butter (500 g)", "Fruit Jams (500 g)",
            "Fresh Milk (1 L)", "Curd (500 g)", "Paneer (200 g)", "Butter (500 g)", "Cheese slices (200 g)", "Fresh Cream (200 g)",
            "Onions (1 kg)", "Potatoes (1 kg)", "Tomatoes (1 kg)", "Leafy greens (1 bnc)", "Apples (1 kg)", "Bananas (1 Doz)"
    );
    public static final List<String> COLD_DRINKS = List.of(
            "Coca-Cola", "Thums Up", "Pepsi", "Diet Coke", "Coke Zero",
            "Sprite", "7Up", "Limca", "Mountain Dew", "Fanta", "Mirinda",
            "Lahori Zeera", "Jeera Masala Soda", "Bindu Fizz",
            "Maaza", "Frooti", "Slice",
            "Real Fruit Juice", "B Natural", "Tropicana",
            "Appy Fizz", "Nimbooz",
            "Red Bull", "Monster Energy", "Sting",
            "Gatorade",
            "Bisleri Water", "Kinley Water", "Aquafina Water", "Club Soda",
            "Amul Cool Milk", "Badam Milk", "Lassi", "Buttermilk (Chaas)"
    );

    // ── Prices (Rs.) ──────────────────────────────────────────────────────────
    public static final Map<String, Double> PRICES;
    static {
        Map<String, Double> m = new LinkedHashMap<>();
        m.put("Gel Face Wash", 150.0);
        m.put("Foam Face Wash", 180.0);
        m.put("Body Wash", 220.0);
        m.put("Bathing Soap", 60.0);
        m.put("Body Lotion", 250.0);
        m.put("Face Cream", 180.0);
        m.put("Sunscreen (SPF 30)", 280.0);
        m.put("Sunscreen (SPF 50)", 350.0);
        m.put("Petroleum Jelly", 80.0);
        m.put("Shaving Cream", 120.0);
        m.put("Razor", 150.0);
        m.put("Deodorant", 200.0);
        m.put("Body Mist", 350.0);
        m.put("Anti-dandruff Shampoo", 180.0);
        m.put("Color-protect Shampoo", 220.0);
        m.put("Hydrating Shampoo", 190.0);
        m.put("Hair Conditioner", 200.0);
        m.put("Coconut Hair Oil", 120.0);
        m.put("Almond Hair Oil", 160.0);
        m.put("Argan Hair Oil", 300.0);
        m.put("Hair Serum", 250.0);
        m.put("Hair Styling Gel", 110.0);
        m.put("Foundation", 450.0);
        m.put("BB/CC Cream", 300.0);
        m.put("Compact Powder", 250.0);
        m.put("Concealer", 350.0);
        m.put("Blush", 280.0);
        m.put("Kajal/Kohl Pencil", 150.0);
        m.put("Liquid Eyeliner", 180.0);
        m.put("Gel Eyeliner", 220.0);
        m.put("Mascara", 260.0);
        m.put("Eyeshadow Palette", 600.0);
        m.put("Matte Lipstick", 350.0);
        m.put("Liquid Lip Color", 400.0);
        m.put("Lip Gloss", 250.0);
        m.put("Tinted Lip Balm", 150.0);
        m.put("Nail Polish", 120.0);
        m.put("Nail Polish Remover", 80.0);

        m.put("Atta (1 kg)", 50.0);
        m.put("Maida (1 kg)", 45.0);
        m.put("Sooji (500 g)", 35.0);
        m.put("Besan (500 g)", 45.0);
        m.put("Basmati Rice (1 kg)", 90.0);
        m.put("Jasmine Rice (1 kg)", 150.0);
        m.put("Brown Rice (1 kg)", 120.0);
        m.put("Oats (500 g)", 85.0);
        m.put("Poha (500 g)", 40.0);
        m.put("Toor dal (1 kg)", 140.0);
        m.put("Moong dal (1 kg)", 120.0);
        m.put("Masoor dal (1 kg)", 95.0);
        m.put("Chana dal (1 kg)", 85.0);
        m.put("Chickpeas (1 kg)", 110.0);
        m.put("Kidney beans (1 kg)", 130.0);
        m.put("Mustard oil (1 L)", 160.0);
        m.put("Sunflower oil (1 L)", 145.0);
        m.put("Olive oil (1 L)", 850.0);
        m.put("Rice bran oil (1 L)", 155.0);
        m.put("Desi Ghee (1 kg)", 650.0);
        m.put("Turmeric (250 g)", 60.0);
        m.put("Chilli powder (250 g)", 75.0);
        m.put("Coriander (250 g)", 55.0);
        m.put("Garam Masala (250 g)", 90.0);
        m.put("Cumin (250 g)", 110.0);
        m.put("Iodized Salt (1 kg)", 25.0);
        m.put("White sugar (1 kg)", 45.0);
        m.put("Brown sugar (1 kg)", 75.0);
        m.put("Jaggery (1 kg)", 65.0);
        m.put("Honey (500 g)", 180.0);
        m.put("Maggi (100 g)", 14.0);
        m.put("Yippee (100 g)", 12.0);
        m.put("Macaroni (500 g)", 65.0);
        m.put("Spaghetti (500 g)", 85.0);
        m.put("Vermicelli (500 g)", 45.0);
        m.put("Cornflakes (500 g)", 150.0);
        m.put("Muesli (500 g)", 250.0);
        m.put("Granola (500 g)", 350.0);
        m.put("Potato chips (100 g)", 35.0);
        m.put("Namkeen (200 g)", 50.0);
        m.put("Cookies (200 g)", 60.0);
        m.put("Marie biscuits (200 g)", 40.0);
        m.put("Crackers (200 g)", 35.0);
        m.put("Tomato ketchup (500 g)", 120.0);
        m.put("Mayonnaise (500 g)", 140.0);
        m.put("Schezwan chutney (250 g)", 85.0);
        m.put("Soy sauce (250 g)", 55.0);
        m.put("Peanut butter (500 g)", 190.0);
        m.put("Fruit Jams (500 g)", 130.0);
        m.put("Fresh Milk (1 L)", 66.0);
        m.put("Curd (500 g)", 45.0);
        m.put("Paneer (200 g)", 85.0);
        m.put("Butter (500 g)", 260.0);
        m.put("Cheese slices (200 g)", 145.0);
        m.put("Fresh Cream (200 g)", 75.0);
        m.put("Onions (1 kg)", 40.0);
        m.put("Potatoes (1 kg)", 35.0);
        m.put("Tomatoes (1 kg)", 50.0);
        m.put("Leafy greens (1 bnc)", 20.0);
        m.put("Apples (1 kg)", 150.0);
        m.put("Bananas (1 Doz)", 60.0);

        m.put("Coca-Cola", 40.0);
        m.put("Thums Up", 40.0);
        m.put("Pepsi", 40.0);
        m.put("Diet Coke", 60.0);
        m.put("Coke Zero", 60.0);
        m.put("Sprite", 40.0);
        m.put("7Up", 40.0);
        m.put("Limca", 40.0);
        m.put("Mountain Dew", 40.0);
        m.put("Fanta", 40.0);
        m.put("Mirinda", 40.0);
        m.put("Lahori Zeera", 20.0);
        m.put("Jeera Masala Soda", 20.0);
        m.put("Bindu Fizz", 20.0);
        m.put("Maaza", 40.0);
        m.put("Frooti", 40.0);
        m.put("Slice", 40.0);
        m.put("Real Fruit Juice", 110.0);
        m.put("B Natural", 110.0);
        m.put("Tropicana", 120.0);
        m.put("Appy Fizz", 35.0);
        m.put("Nimbooz", 25.0);
        m.put("Red Bull", 115.0);
        m.put("Monster Energy", 115.0);
        m.put("Sting", 20.0);
        m.put("Gatorade", 50.0);
        m.put("Bisleri Water", 20.0);
        m.put("Kinley Water", 20.0);
        m.put("Aquafina Water", 20.0);
        m.put("Club Soda", 20.0);
        m.put("Amul Cool Milk", 35.0);
        m.put("Badam Milk", 40.0);
        m.put("Lassi", 25.0);
        m.put("Buttermilk (Chaas)", 15.0);
        PRICES = Collections.unmodifiableMap(m);
    }

    // ── Tax rates ─────────────────────────────────────────────────────────────
    public static final double COSMETIC_TAX_RATE   = 0.05;
    public static final double GROCERY_TAX_RATE    = 0.10;
    public static final double COLD_DRINK_TAX_RATE = 0.05;

    // ── Singleton ──────────────────────────────────────────────────────────────
    private static BillManager instance;
    private BillManager() {}
    public static synchronized BillManager getInstance() {
        if (instance == null) instance = new BillManager();
        return instance;
    }

    // ── State ──────────────────────────────────────────────────────────────────
    private final Map<Integer, Bill> bills = new LinkedHashMap<>();
    private int nextBillNumber = 4000 + (int)(Math.random() * 100);

    public int getNextBillNumber() { return nextBillNumber; }

    public Bill generateBill(String customerName, String phone, List<Product> products) {
        List<Product> purchased = products.stream()
                .filter(p -> p.getQuantity() > 0).toList();

        double cosTotal  = purchased.stream().filter(p -> COSMETICS.contains(p.getName()))
                .mapToDouble(Product::getTotal).sum();
        double groTotal  = purchased.stream().filter(p -> GROCERY.contains(p.getName()))
                .mapToDouble(Product::getTotal).sum();
        double cdTotal   = purchased.stream().filter(p -> COLD_DRINKS.contains(p.getName()))
                .mapToDouble(Product::getTotal).sum();

        double cosTax = cosTotal  * COSMETIC_TAX_RATE;
        double groTax = groTotal  * GROCERY_TAX_RATE;
        double cdTax  = cdTotal   * COLD_DRINK_TAX_RATE;

        Bill bill = new Bill(nextBillNumber++, customerName, phone,
                             purchased, cosTax, groTax, cdTax);
        bills.put(bill.getBillNumber(), bill);
        return bill;
    }

    public Optional<Bill> findBill(int billNumber) {
        return Optional.ofNullable(bills.get(billNumber));
    }
}
