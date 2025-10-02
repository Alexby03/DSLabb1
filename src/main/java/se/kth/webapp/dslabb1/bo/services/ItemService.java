package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.db.DBManager;
import se.kth.webapp.dslabb1.db.data.ItemDAO;
import se.kth.webapp.dslabb1.bo.models.Item;
import se.kth.webapp.dslabb1.bo.models.enums.Result;

import java.sql.Connection;
import java.util.*;

import javax.swing.plaf.PanelUI;

public class ItemService {

    public static Result createOrderItem(UUID orderId, String sku, String productName, double price ,int quantity) {

        if(orderId == null || sku == null || sku.isBlank() || quantity <=0) return Result.FAILED;

        try (Connection conn = DBManager.getConnection()){
            Item item = new Item(orderId, sku, productName, price, quantity);
            return ItemDAO.createItem(conn,item);

        } catch (Exception e) {
            System.err.println("Error creating Order Item: "+e.getMessage());
            return Result.FAILED;
        }
    }

    public static List<ItemDAO> getOrderItems(UUID orderId) {
        if (orderId == null) return null;

        try {
            return ItemDAO.findByOrderId(orderId);

        } catch (Exception e) {
            System.err.println("Error getting Order Items: "+e.getMessage());
            return null;
        }
    }

    public static List<Item> getOrderItemsWithDetails(UUID orderId) {

        if (orderId == null) {
            return new ArrayList<>();
        }

        try {
            return ItemDAO.findItemsWithProductDetails(orderId);
        } catch (Exception e) {
            System.err.println("Error getting order items with details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}
