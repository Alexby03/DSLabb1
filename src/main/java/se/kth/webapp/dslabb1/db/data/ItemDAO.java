package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.Item;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.db.DBManager;

import java.sql.*;
import java.util.*;

/**
 * DAO record for Item operations - matches T_Item table structure
 */
public record ItemDAO(
        UUID orderId,
        String sku,
        int quantity
) {

    /**
     * Create a new item
     */
    public static Result createItem(Connection conn, Item item) throws SQLException {

        String sql = "INSERT INTO T_Item (orderId, sku, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getOrderId().toString());
            ps.setString(2, item.getSku());
            ps.setInt(3, item.getQuantity());
            return ps.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;
        }
    }


    /**
     * Find items by order ID with product information
     */
    public static List<ItemDAO> findByOrderId(UUID orderId) {
        List<ItemDAO> items = new ArrayList<>();
        String sql = "SELECT orderId, sku, quantity FROM T_Item WHERE orderId = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new ItemDAO(
                            UUID.fromString(rs.getString("orderId")),
                            rs.getString("sku"),
                            rs.getInt("quantity")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding items by order: " + e.getMessage());
        }

        return items;
    }

    /**
     * Find items with product details (JOIN query)
     */
    public static List<Item> findItemsWithProductDetails(UUID orderId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.orderId, i.sku, i.quantity, p.productName, p.price " +
                "FROM T_Item i JOIN T_Product p ON i.sku = p.sku " +
                "WHERE i.orderId = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new Item(
                            UUID.fromString(rs.getString("orderId")),
                            rs.getString("sku"),
                            rs.getString("productName"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding items with product details: " + e.getMessage());
        }

        return items;
    }
}
