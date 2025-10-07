package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.OrderStatus;
import se.kth.webapp.dslabb1.bo.models.enums.Result;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Class representing the Order class and object.
 */
public class Order implements Serializable {
    private final UUID orderId;
    private final UUID customerId;
    private final LocalDateTime dateOfPurchase;
    private final List<Item> items;
    private OrderStatus orderStatus;

    /**
     * Reconstructs an order from the database.
     *
     * @param orderId
     * @param customerId
     * @param items          products ordered
     * @param dateOfPurchase the date of the purchase that created the order
     * @param orderStatus
     */
    public Order(UUID orderId, UUID customerId, List<Item> items, LocalDateTime dateOfPurchase, OrderStatus orderStatus) {
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        if (items.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("null item");
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.dateOfPurchase = dateOfPurchase;
        this.orderStatus = orderStatus;
    }

    /**
     * Creates a new order upon purchase.
     *
     * @param customerId
     * @param items      products ordered
     */
    public Order(UUID customerId, List<CartItem> items) {
        this.orderId = UUID.randomUUID();
        this.customerId = customerId;
        this.items = new ArrayList<>();
        for (CartItem cartItem : items) {
            this.items.add(new Item(this.orderId, cartItem.getSku(),
                    cartItem.getProductName(), cartItem.getPrice(), cartItem.getQuantity()));
        }
        this.dateOfPurchase = LocalDateTime.now();
        this.orderStatus = OrderStatus.PAID;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LocalDateTime getDateOfPurchase() {
        return dateOfPurchase;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Fetches the total amount of all products (items) ordered.
     *
     * @return the total amount.
     */
    public double getTotalAmount() {
        return items.stream()
                .mapToDouble(Item::subtotal)
                .sum();
    }

    /**
     * Advances the status of the order.
     *
     * @return if advance was successful.
     */
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
            default -> {
                return Result.FAILED;
            }
        }
    }

    /**
     * Marks this order instance as canceled.
     *
     * @return if cancellation was successful.
     */
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
