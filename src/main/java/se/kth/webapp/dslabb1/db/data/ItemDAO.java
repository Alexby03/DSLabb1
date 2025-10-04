package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.Item;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.db.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data access object for an entity in table T_Item in DB schema.
 */
public record ItemDAO(
        UUID orderId,
        String sku,
        int quantity
) {

    /**
     * Generates a new item to the database.
     *
     * @param conn the connection to the database.
     * @param item the item instance to copy from.
     * @return whether creating a product was successful or not.
     */
    public static Result createItem(Connection conn, Item item) throws SQLException {

        String sql = "INSERT INTO T_Item (orderId, sku, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.orderId().toString());
            ps.setString(2, item.sku());
            ps.setInt(3, item.quantity());
            return ps.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;
        }
    }

    /**
     * Attempts to find an item matching a certain order.
     *
     * @param orderId of the order containing the item.
     * @return list of item data access objects inside the order queried against.
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
     * Attempts to find an item with details of the product inside the item matching a certain order.
     *
     * @param orderId of the order containing the item.
     * @return a list of item instances inside the order queried against.
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
