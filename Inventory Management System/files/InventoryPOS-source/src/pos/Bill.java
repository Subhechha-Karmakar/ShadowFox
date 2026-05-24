package pos;

import java.util.List;

public class Bill {
    private final int billNumber;
    private final String customerName;
    private final String phoneNumber;
    private final List<Product> items;
    private final double cosmeticTax;
    private final double groceryTax;
    private final double coldDrinkTax;

    public Bill(int billNumber, String customerName, String phoneNumber,
                List<Product> items,
                double cosmeticTax, double groceryTax, double coldDrinkTax) {
        this.billNumber   = billNumber;
        this.customerName = customerName;
        this.phoneNumber  = phoneNumber;
        this.items        = items;
        this.cosmeticTax  = cosmeticTax;
        this.groceryTax   = groceryTax;
        this.coldDrinkTax = coldDrinkTax;
    }

    public int    getBillNumber()   { return billNumber; }
    public String getCustomerName() { return customerName; }
    public String getPhoneNumber()  { return phoneNumber; }
    public List<Product> getItems() { return items; }
    public double getCosmeticTax()  { return cosmeticTax; }
    public double getGroceryTax()   { return groceryTax; }
    public double getColdDrinkTax() { return coldDrinkTax; }

    public double getTotalCosmeticPrice() {
        return items.stream().filter(p -> BillManager.COSMETICS.contains(p.getName()))
                .mapToDouble(Product::getTotal).sum();
    }
    public double getTotalGroceryPrice() {
        return items.stream().filter(p -> BillManager.GROCERY.contains(p.getName()))
                .mapToDouble(Product::getTotal).sum();
    }
    public double getTotalColdDrinkPrice() {
        return items.stream().filter(p -> BillManager.COLD_DRINKS.contains(p.getName()))
                .mapToDouble(Product::getTotal).sum();
    }
    public double getGrandTotal() {
        return getTotalCosmeticPrice() + getTotalGroceryPrice() + getTotalColdDrinkPrice()
             + cosmeticTax + groceryTax + coldDrinkTax;
    }
}
