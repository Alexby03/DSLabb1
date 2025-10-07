package se.kth.webapp.dslabb1.ui.info;

import se.kth.webapp.dslabb1.bo.models.enums.OrderStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public record OrderInfo(UUID orderId,  UUID customerId,  LocalDateTime dateOfPurchase,  List<ItemInfo> items,  OrderStatus orderStatus,
                        double totalAmount) implements Serializable {

    public OrderInfo {
        if (orderId == null) throw new IllegalArgumentException("orderId required");
        if (customerId == null) throw new IllegalArgumentException("customerId required");
    }

    public int getItemCount() {
        int  count = 0;
        for (ItemInfo item : items) count += item.quantity();
        return count;
    }
}

