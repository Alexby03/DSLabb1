package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.Item;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.db.DBManager;
import se.kth.webapp.dslabb1.db.data.ItemDAO;
import se.kth.webapp.dslabb1.ui.info.ItemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class providing methods for handling items to the presentation layer.
 */
public class ItemService {

    public static ItemInfo toItemInfo(Item item) {
        if(item == null) return null;
        return new ItemInfo(item.orderId(), item.sku(), item.productName(), item.unitPrice(), item.quantity());
    }

    /**
     * Generates a new item belonging to an order to the database.
     *
     * @param orderId     id of the order the item will belong to.
     * @param sku         the product id the item contains.
     * @param productName the name of the product the item contains.
     * @param price       the price of the product the item contains.
     * @param quantity    the quantity of the product the item contains.
     * @return whether creating a product was successful or not.
     */
    public static Result createOrderItem(UUID orderId, String sku, String productName, double price, int quantity) {

        if (orderId == null || sku == null || sku.isBlank() || quantity <= 0) return Result.FAILED;

        try (DBManager db = DBManager.open()) {
            Item item = new Item(orderId, sku, productName, price, quantity);
            return ItemDAO.createItem(db.getConnection(), item);

        } catch (Exception e) {
            System.err.println("Error creating Order Item: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Attempts to retrieve all items within an order.
     *
     * @param orderId the id of the order the items belong to.
     * @return a list of item data access objects within the order.
     */
    public static List<ItemDAO> getOrderItems(UUID orderId) {
        if (orderId == null) return null;

        try {
            return ItemDAO.findByOrderId(orderId);

        } catch (Exception e) {
            System.err.println("Error getting Order Items: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attempts to find an item with details of the product inside the item matching a certain order.
     *
     * @param orderId of the order containing the item.
     * @return a list of item instances inside the order queried against.
     */
    public static List<ItemInfo> getOrderItemsWithDetails(UUID orderId) {

        if (orderId == null) return new ArrayList<>();

        try {
            List<Item> items = ItemDAO.findItemsWithProductDetails(orderId);
            return items.stream().map(ItemService::toItemInfo).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting order items with details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}
