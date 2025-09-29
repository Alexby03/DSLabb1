package se.kth.webapp.dslabb1.models;

import se.kth.webapp.dslabb1.models.enums.Result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Item is created for an Order. It has an itemId (UUID) generated on order creation.
 * It contains a snapshot of product data (sku, name, unitPrice, quantity).
 */
public class Cart implements Serializable {
    private final UUID customerId;
    private List<CartItem> items;

    public Cart(UUID customerId, String sku, List<CartItem> items) {
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
    }

    public UUID getCustomerId() { return customerId; }

    public List<CartItem> getItems(){ return List.copyOf(items); }

    public int nrProducts(){
        return items.size();
    }

    public double totalCost(){
        double totalCost = 0;
        for (CartItem item : items) {
            totalCost += item.getPrice() * item.getQuantity();
        }
        return totalCost;
    }

    public Result emptyCart(){
        items.clear();
        return Result.SUCCESS;
    }

//    public Result addItem(CartItem item){
//        items.add(item);
//        return Result.SUCCESS;
//    }

    // Check if item with same SKU exists in Cart, if so, increase quantity, if not add as new item
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
