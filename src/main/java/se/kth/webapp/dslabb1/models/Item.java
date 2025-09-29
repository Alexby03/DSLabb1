package se.kth.webapp.dslabb1.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Item is created for an Order. It has an itemId (UUID) generated on order creation.
 * It contains a snapshot of product data (sku, name, unitPrice, quantity).
 */
public class Item implements Serializable {
    private final UUID itemId;
    private final UUID orderId;
    private final String sku;
    private final String productName;
    private final double unitPrice;
    private final int quantity;

    public Item(UUID orderId, String sku, String productName, double unitPrice, int quantity) {
        if (orderId == null) throw new IllegalArgumentException("orderId required");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        if (productName == null || productName.isBlank()) throw new IllegalArgumentException("productName required");
        this.itemId = UUID.randomUUID();
        this.orderId = orderId;
        this.sku = sku;
        this.productName = productName;
        this.unitPrice = Math.max(unitPrice, 0.0);
        this.quantity = Math.max(1, quantity);
    }

    public UUID getItemId() { return itemId; }
    public UUID getOrderId() { return orderId; }
    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public double subtotal() { return unitPrice * quantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equals(itemId, item.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return "Item{" + "itemId=" + itemId + ", sku='" + sku + '\'' + ", name='" + productName + '\'' + ", qty=" + quantity + '}';
    }
}
