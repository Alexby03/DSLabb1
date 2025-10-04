package se.kth.webapp.dslabb1.bo.models;

/**
 * Represents an item that is currently in one customer's cart.
 */
public class CartItem {
    private final String sku;
    private final String productName;
    private final double price;
    private int quantity;

    /**
     * Creates or reconstructs a new cartItem from the database.
     *
     * @param sku         of the product in the Item.
     * @param productName of the product in the Item.
     * @param price       of the product in the Item.
     * @param quantity    of how many of a given product bought represented by the Item.
     */
    public CartItem(String sku, String productName, double price, int quantity) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity required");
        this.sku = sku;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product in an Item.
     *
     * @param quantity of the product.
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity required");
        this.quantity = quantity;
    }

    /**
     * Increases the quantity of the product in an Item.
     *
     * @param quantity of the product.
     */
    public void addQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity required");
        this.quantity += quantity;
    }

    /**
     * Fetches the total cost of the amount of products for this particular item in cart.
     *
     * @return the subtotal of the items' price.
     */
    public double subtotal() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "SKU=" + sku +
                ", products=" + productName +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

}
