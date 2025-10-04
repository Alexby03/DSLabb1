package se.kth.webapp.dslabb1.bo.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class representing the Item class and object.
 * An Item is a logged cartItem within the database and is purely used for auditing.
 * Item is created for a given Order. Contains a snapshot of product data (sku, name, unitPrice, quantity).
 */
public record Item(UUID orderId, String sku, String productName, double unitPrice,
                   int quantity) implements Serializable {
    /**
     * Reconstructs an item from the database.
     *
     * @param orderId     the id of the order the item was purchased in.
     * @param sku         the unique ID for the product purchased within the item.
     * @param productName name of the product.
     * @param unitPrice   price of the product bought.
     * @param quantity    how many instances of the product was purchased.
     */
    public Item {
        if (orderId == null) throw new IllegalArgumentException("orderId required");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        if (productName == null || productName.isBlank()) throw new IllegalArgumentException("productName required");
    }

    public UUID getOrderId() { return orderId; }
    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    /**
     * Fetches the total cost of the amount of products for this particular item.
     *
     * @return the subtotal of the items' price.
     */
    public double subtotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return "Item{" + ", sku='" + sku + '\'' + ", name='" + productName + '\'' + ", qty=" + quantity + '}';
    }
}
