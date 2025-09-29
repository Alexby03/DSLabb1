package se.kth.webapp.dslabb1.models;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private final BigDecimal unitPrice;
    private final int quantity;

    public Item(UUID orderId, String sku, String productName, BigDecimal unitPrice, int quantity) {
        this.itemId = UUID.randomUUID();
        this.orderId = orderId;
        this.sku = sku;
        this.productName = productName;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.quantity = Math.max(1, quantity);
    }

    public UUID getItemId() { return itemId; }
    public UUID getOrderId() { return orderId; }
    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public BigDecimal subtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
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
