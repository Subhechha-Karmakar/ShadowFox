package pos;

public class Product {
    private final String name;
    private final double price;
    private int quantity;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
        this.quantity = 0;
    }

    public String getName()     { return name; }
    public double getPrice()    { return price; }
    public int getQuantity()    { return quantity; }
    public void setQuantity(int q) { this.quantity = Math.max(0, q); }
    public double getTotal()    { return price * quantity; }
}
