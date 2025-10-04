package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.Cart;
import se.kth.webapp.dslabb1.bo.models.CartItem;
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
 * Data access object for entities in table T_Cart in DB schema.
 * A cart data access objects represents a given product currently in a
 * customer's cart. To properly represent an entire cart, a list of cart
 * data access objects is used.
 */
public record CartDAO(
        UUID userId,
        String sku,
        int quantity
) {

    /**
     * Adds an item to a customer's cart.
     *
     * @param userId   if of the customer.
     * @param sku      the product id.
     * @param quantity amount of said product.
     * @return whether adding the product to cart was successful.
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
     * Attempts to find a cart for a customer.
     * Cart is presented as a list of cart data access objects.
     *
     * @param userId id of the cart's customer.
     * @return list of cart data access objects within that cart.
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
     * Attempts to find a cart for a customer.
     * Cart is presented as a list of cart item instances containing further product details.
     *
     * @param userId id of the cart's customer.
     * @return list of cart item instances within that cart.
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
     * Attempts to find a specific cart item for a customer.
     *
     * @param userId id of the customer
     * @param sku    id of the product within the item searched.
     * @return a cart item matching the query.
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

            if (!rs.next())
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
     * Attempts to update the quantity of a given product within an item in a cart.
     *
     * @param userId      the ID of the customer owning the cart.
     * @param sku         the product within the item.
     * @param newQuantity of the product within the item.
     * @return whether updating the quantity was successful or not.
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
     * Attempts to remove an item from the cart.
     *
     * @param userId the ID of the customer owning the cart.
     * @param sku    the product within the item.
     * @return whether removing the item from the cart was successful or not.
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
     * Attempts to clear the cart of a given customer.
     *
     * @param userId id of the customer owning the cart.
     * @param conn   connection to the database.
     * @return whether clearing the cart was successful or not.
     * @throws SQLException when an error occurs on the database side.
     */
    public static Result clearCart(UUID userId, Connection conn) throws SQLException {
        String sql = "DELETE FROM T_Cart WHERE userId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        }
    }

    /**
     * Attempts to retrieve a customer's cart.
     *
     * @param userId id of the cart's customer.
     * @return the cart for the given user,
     */
    public static Cart getCartForUser(UUID userId) {
        List<CartItem> cartItems = findCartItemsWithProductDetails(userId);
        return new Cart(userId, cartItems);
    }
}
