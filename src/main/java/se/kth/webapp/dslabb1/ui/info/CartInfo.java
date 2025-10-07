package se.kth.webapp.dslabb1.ui.info;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;


public record CartInfo(
        UUID customerId,
        List<CartItemInfo> items
) implements Serializable {


    public CartInfo {
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        if (items != null) items = List.copyOf(items);
        else items = List.of();
    }

    public int getNrItems() {
        return items.size();
    }

    public double getTotalCost() {
        return items.stream()
                .mapToDouble(CartItemInfo::getSubtotal)
                .sum();
    }
}

