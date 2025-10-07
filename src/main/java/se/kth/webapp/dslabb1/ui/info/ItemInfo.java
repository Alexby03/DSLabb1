package se.kth.webapp.dslabb1.ui.info;

import java.io.Serializable;
import java.util.UUID;

public record ItemInfo( UUID orderId, String sku,  String productName,  double unitPrice,  int quantity) implements Serializable {


    public ItemInfo {
        if (orderId == null) throw new IllegalArgumentException("orderId required");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
