package se.kth.webapp.dslabb1.models;

import se.kth.webapp.dslabb1.models.enums.OrderStatus;
import se.kth.webapp.dslabb1.models.enums.Result;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Order contains generated orderId and a list of Items (each Item has its own itemId).
 */
public class Order implements Serializable {
    private final UUID orderId;
    private final UUID customerId;
    private final LocalDateTime dateOfPurchase;
    private OrderStatus orderStatus;
    private final List<Item> items;

    public Order(UUID orderId, UUID customerId, List<Item> items, LocalDateTime dateOfPurchase, OrderStatus orderStatus) {
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("items required");
        if (items.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("null item");
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.dateOfPurchase = dateOfPurchase;
        this.orderStatus = orderStatus;
    }

    public UUID getOrderId() { return orderId; }
    public UUID getCustomerId() { return customerId; }
    public LocalDateTime getDateOfPurchase() { return dateOfPurchase; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    private void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }

    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(Item::subtotal)
                .sum();
    }

    public Result advanceStatus() {
        if (orderStatus == OrderStatus.CANCELED || orderStatus == OrderStatus.DELIVERED) return Result.FAILED;
        switch (orderStatus) {
            case PAID -> {
                orderStatus = OrderStatus.SHIPPED;
                return Result.SUCCESS;
            }
            case SHIPPED -> {
                orderStatus = OrderStatus.DELIVERED;
                return Result.SUCCESS;
            }
            default -> {return Result.FAILED;}
        }
    }

    public Result cancelOrder() {
        if (orderStatus == OrderStatus.SHIPPED || orderStatus == OrderStatus.DELIVERED) return Result.FAILED;
        orderStatus = OrderStatus.CANCELED;
        return Result.SUCCESS;
    }

    @Override
    public String toString() {
        return "Order{" + "orderId=" + orderId + ", customerId=" + customerId + ", items=" + items + ", status=" + orderStatus + '}';
    }
}
