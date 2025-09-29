package se.kth.webapp.dslabb1.models;

import se.kth.webapp.dslabb1.models.enums.OrderStatus;

import java.io.Serializable;
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
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("items required");
        if (items.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("null item");
        this.orderId = UUID.randomUUID();
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.createdAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.PAID;
    }

    public UUID getOrderId() { return orderId; }
    public UUID getCustomerId() { return customerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }

    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(Item::subtotal)
                .sum();
    }

    public void advanceStatus() {
        if (orderStatus == OrderStatus.CANCELED || orderStatus == OrderStatus.DELIVERED) return;
        switch (orderStatus) {
            case PAID -> orderStatus = OrderStatus.SHIPPED;
            case SHIPPED -> orderStatus = OrderStatus.DELIVERED;
            default -> {}
        }
    }

    public boolean cancelOrder() {
        if (orderStatus == OrderStatus.SHIPPED || orderStatus == OrderStatus.DELIVERED) return false;
        orderStatus = OrderStatus.CANCELED;
        return true;
    }

    @Override
    public String toString() {
        return "Order{" + "orderId=" + orderId + ", customerId=" + customerId + ", items=" + items + ", status=" + orderStatus + '}';
    }
}
