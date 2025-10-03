package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.*;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.db.DBManager;

import java.sql.*;
import java.util.*;

/**
 * DAO record for Cart operations - matches T_Cart table structure
 */
public record CartDAO(
        UUID userId,
        String sku,
        int quantity
) {

    /**
     * Add item to cart (or update quantity if exists)
     */
    public static Result addItemToCart(UUID userId, String sku, int quantity) {
        String sql = "INSERT INTO T_Cart (userId, sku, quantity) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());
            stmt.setString(2, sku);
            stmt.setInt(3, quantity);

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Find cart items by user ID
     */
    public static List<CartDAO> findByUserId(UUID userId) {
        List<CartDAO> cartItems = new ArrayList<>();
        String sql = "SELECT * FROM T_Cart WHERE userId = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cartItems.add(new CartDAO(
                            UUID.fromString(rs.getString("userId")),
                            rs.getString("sku"),
                            rs.getInt("quantity")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding cart items: " + e.getMessage());
        }

        return cartItems;
    }

    /**
     * Find cart items with product details (JOIN query)
     */
    public static List<CartItem> findCartItemsWithProductDetails(UUID userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT c.sku, c.quantity, p.productName, p.price " +
                "FROM T_Cart c JOIN T_Product p ON c.sku = p.sku " +
                "WHERE c.userId = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cartItems.add(new CartItem(
                            rs.getString("sku"),
                            rs.getString("productName"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding cart items with product details: " + e.getMessage());
        }

        return cartItems;
    }

    /**
     * Looks for a cartItem to return
     * @param userId id of cart owner
     * @param sku id of product
     * @return the cartItem, else null if such item does not exist in cart
     */
    public static CartItem findCartItemBySKU(UUID userId, String sku) {
        String sql = "SELECT c.sku, c.quantity, p.productName, p.price " +
                "FROM T_Cart c JOIN T_Product p ON c.sku = p.sku " +
                "WHERE c.userId = ? AND c.sku = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());
            stmt.setString(2, sku);

            ResultSet rs = stmt.executeQuery();

            if(!rs.next())
                return null;

            return new CartItem(
                    rs.getString("sku"),
                    rs.getString("productName"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update cart item quantity
     */
    public static Result updateCartItemQuantity(UUID userId, String sku, int newQuantity) {
        if (newQuantity <= 0) {
            return removeItemFromCart(userId, sku);
        }

        String sql = "UPDATE T_Cart SET quantity = ? WHERE userId = ? AND sku = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setString(2, userId.toString());
            stmt.setString(3, sku);

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error updating cart item quantity: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Remove item from cart
     */
    public static Result removeItemFromCart(UUID userId, String sku) {
        String sql = "DELETE FROM T_Cart WHERE userId = ? AND sku = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());
            stmt.setString(2, sku);

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error removing item from cart: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Clear entire cart for a user
     */
    public static Result clearCart(UUID userId, Connection conn) throws SQLException{
        String sql = "DELETE FROM T_Cart WHERE userId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        }
    }

    /**
     * Get cart for a user (reconstruct Cart object from database)
     */
    public static Cart getCartForUser(UUID userId) {
        List<CartItem> cartItems = findCartItemsWithProductDetails(userId);
        return new Cart(userId, cartItems);
    }
}
