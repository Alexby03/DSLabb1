package se.kth.webapp.dslabb1.ui.info;

import java.io.Serializable;


public record CartItemInfo(
        String sku,
        String productName,
        double price,
        int quantity
) implements Serializable {


    public CartItemInfo {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
    }

    public double getSubtotal() {
        return price * quantity;
    }
}

