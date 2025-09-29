package se.kth.webapp.dslabb1.models;

public class CartItem {
    private final String sku;
    private final String productName;
    private final double price;
    private int quantity;

    public  CartItem(String sku, String productName, double price,  int quantity) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity required");
        this.sku = sku;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity required");
        this.quantity = quantity; }

    public void addQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity required");
        this.quantity += quantity;
    }

    public double subtotal() {
        return price * quantity;
    }

@Override
    public String toString() {
    return "Cart{" +
            "SKU=" + sku +
            ", products=" + productName + ", price=" + price +
            '}';
}

}
