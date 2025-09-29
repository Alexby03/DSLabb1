package se.kth.webapp.dslabb1.models;

import se.kth.webapp.dslabb1.models.enums.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Order contains generated orderId and a list of Items (each Item has its own itemId).
 */
public class Order implements Serializable {
    private final UUID orderId;
    private final UUID customerId;
    private final LocalDateTime createdAt;
    private OrderStatus orderStatus;
    private final List<Item> items;

    public Order(UUID customerId, List<Item> items) {
        this.orderId = UUID.randomUUID();
        this.customerId = customerId;
        this.items = new ArrayList<>(items != null ? items : Collections.emptyList());
        this.createdAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.PAID;
    }

    public UUID getOrderId() { return orderId; }
    public UUID getCustomerId() { return customerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(Item::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCELED;
    }

    public void updateOrderStatus() {
        OrderStatus[] statuses = OrderStatus.values();
        int currentIndex = orderStatus.ordinal();
        if (currentIndex < statuses.length - 1) {
            this.orderStatus = statuses[currentIndex + 1];
        }
    }

    @Override
    public String toString() {
        return "Order{" + "orderId=" + orderId + ", customerId=" + customerId + ", items=" + items + ", status=" + orderStatus + '}';
    }
}
