package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.Result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Item is created for an Order. It has an itemId (UUID) generated on order creation.
 * It contains a snapshot of product data (sku, name, unitPrice, quantity).
 */
public record Cart(UUID customerId, List<CartItem> items) implements Serializable {
    /**
     * Creates or reconstructs a cart from the database.
     *
     * @param customerId ID of the owner of the cart.
     * @param items      the items within the cart.
     */
    public Cart(UUID customerId, List<CartItem> items) {
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
    }

    @Override
    public List<CartItem> items() {
        return List.copyOf(items);
    }

    /**
     *
     * @return the number of items within the cart currently.
     */
    public int nrItems() {
        return items.size();
    }

    /**
     *
     * @return the total cost for all products in all items.
     */
    public double totalCost() {
        double totalCost = 0;
        for (CartItem item : items) {
            totalCost += item.getPrice() * item.getQuantity();
        }
        return totalCost;
    }

    /**
     * Empties the cart of all items.
     *
     * @return success if cleared the cart.
     */
    public Result emptyCart() {
        items.clear();
        return Result.SUCCESS;
    }

    /**
     * Adds an item to the cart.
     *
     * @param newItem
     * @return whether adding was successful.
     */
    public Result addItem(CartItem newItem) {
        if (newItem == null) {
            return Result.FAILED;
        }

        for (CartItem existingItem : items) {
            if (existingItem.getSku().equals(newItem.getSku())) {
                existingItem.addQuantity(newItem.getQuantity());
                return Result.SUCCESS;
            }
        }

        items.add(newItem);
        return Result.SUCCESS;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "customerId=" + customerId +
                ", products=" + items +
                '}';
    }
}
