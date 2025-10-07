package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.Cart;
import se.kth.webapp.dslabb1.bo.models.CartItem;
import se.kth.webapp.dslabb1.db.DataAccessException;
import se.kth.webapp.dslabb1.ui.info.CartInfo;
import se.kth.webapp.dslabb1.ui.info.CartItemInfo;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.DBManager;
import se.kth.webapp.dslabb1.db.data.CartDAO;
import se.kth.webapp.dslabb1.db.data.ProductDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class providing methods for handling carts to the presentation layer.
 */
public class CartService {

    public static CartItemInfo toCartItemInfo(CartItem cartItem) {
        return new CartItemInfo(cartItem.getSku(), cartItem.getProductName(), cartItem.getPrice(), cartItem.getQuantity());
    }

    public static CartInfo toCartInfo(Cart cart) {
        List<CartItemInfo> itemInfoList = cart.items().stream().map(CartService::toCartItemInfo).collect(Collectors.toList());
        return new CartInfo(cart.customerId(), itemInfoList);
    }

    /**
     * Adds an item to a customer's cart.
     *
     * @param userId   if of the customer.
     * @param sku      the product id.
     * @param quantity amount of said product.
     * @param userType if the user actually is a customer.
     * @return whether adding the product to cart was successful.
     */
    public static Result addItemToCart(UUID userId, String sku, int quantity, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType)) return Result.PRIVILEGE;

        if (userId == null || sku == null || sku.isBlank() || quantity <= 0) {
            return Result.FAILED;
        }

        try {
            ProductDAO product = ProductDAO.findBySku(sku);
            if (product == null || product.isRetired()) {
                return Result.FAILED;
            }

            return CartDAO.addItemToCart(userId, sku, quantity);

        } catch (Exception e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Attempts to update the quantity of a given product within an item in a cart.
     *
     * @param userId      the ID of the customer owning the cart.
     * @param sku         the product within the item.
     * @param newQuantity of the product within the item.
     * @param userType    if the user actually is a customer.
     * @return whether updating the quantity was successful or not.
     */
    public static Result updateCartItemQuantity(UUID userId, String sku, int newQuantity, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType)) return Result.PRIVILEGE;

        if (userId == null || sku == null || sku.isBlank() || newQuantity < 0) {
            return Result.FAILED;
        }

        try {
            if (newQuantity == 0) {
                return CartDAO.removeItemFromCart(userId, sku);
            }

            ProductDAO product = ProductDAO.findBySku(sku);
            if (product == null || product.quantity() < newQuantity) {
                return Result.FAILED;
            }

            return CartDAO.updateCartItemQuantity(userId, sku, newQuantity);

        } catch (Exception e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Attempts to remove an item from the cart.
     *
     * @param userId   the ID of the customer owning the cart.
     * @param sku      the product within the item.
     * @param userType if the user actually is a customer.
     * @return whether removing the item from the cart was successful or not.
     */
    public static Result removeItemFromCart(UUID userId, String sku, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType)) return Result.PRIVILEGE;

        if (userId == null || sku == null || sku.isBlank()) {
            return Result.FAILED;
        }

        try {
            return CartDAO.removeItemFromCart(userId, sku);
        } catch (Exception e) {
            System.err.println("Error removing item from cart: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Attempts to clear the cart of a given customer.
     *
     * @param userId   id of the customer owning the cart.
     * @param userType if the user actually is a customer.
     * @return whether clearing the cart was successful or not.
     * @throws DataAccessException when an error occurs on the database side.
     */
    public static Result clearCart(UUID userId, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType)) return Result.PRIVILEGE;

        if (userId == null) {
            return Result.FAILED;
        }

        try (DBManager db = DBManager.open()) {
            return CartDAO.clearCart(userId, db.getConnection());
        } catch (DataAccessException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Attempts to retrieve a customer's cart.
     *
     * @param userId   id of the cart's customer.
     * @param userType if the user actually is a customer.
     * @return the cart for the given user,
     */
    public static CartInfo getUserCart(UUID userId, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType) && !UserType.ADMIN.equals(userType))
            return null;
        if (userId == null) return null;
        try {
            Cart cart = CartDAO.getCartForUser(userId);
            return toCartInfo(cart);
        } catch (Exception e) {
            System.err.println("Error getting user cart: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attempts to retrieve a customer's cart's total cost.
     *
     * @param userId   id of the cart's customer.
     * @param userType if the user actually is a customer.
     * @return the total cost of the items within the cart.
     */
    public static double getCartTotal(UUID userId, UserType userType) {
        Cart cart = getUserCartHelper(userId, userType);
        if (cart == null) {
            return 0.0;
        }

        try {
            double total = 0.0;
            List<CartItem> items = cart.items();
            for (CartItem item : items) {
                total += item.getPrice() * item.getQuantity();
            }

            return total;
        } catch (Exception e) {
            System.err.println("Error calculating cart total: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Counts how many items are in a customer's cart.
     *
     * @param userId   the customer
     * @param userType the userType for customer
     * @return amount of items in cart currently
     */
    public static int getCartItemCount(UUID userId, UserType userType) {
        Cart cart = getUserCartHelper(userId, userType);
        if (cart == null) {
            return 0;
        }

        try {
            int count = 0;
            List<CartItem> items = cart.items();
            for (CartItem item : items) {
                count += item.getQuantity();
            }

            return count;
        } catch (Exception e) {
            System.err.println("Error counting cart items: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Looks for a cartItem to return
     *
     * @param userId id of cart owner
     * @param sku    id of product
     * @return the cartItem, else null if such item does not exist in cart
     */
    public static CartItemInfo getCartItem(UUID userId, String sku) {
        if (userId == null || sku == null || sku.isBlank())
            return null;
        try {
            CartItem cartItem = CartDAO.findCartItemBySKU(userId, sku);
            return toCartItemInfo(cartItem);
        } catch (Exception e) {
            System.err.println("Error getting cart item: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attempts to retrieve all items within a cart.
     *
     * @param userId   id of the owner of the cart.
     * @param userType if the user actually is a customer.
     * @return a list of cartItem instances representing the cart items.
     */
    public static List<CartItemInfo> getAllItems(UUID userId, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType))
            return new ArrayList<>();
        try {
            Cart cart = CartDAO.getCartForUser(userId);
            return cart.items().stream()
                    .map(CartService::toCartItemInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting all cart items: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static Cart getUserCartHelper(UUID userId, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType) && !UserType.ADMIN.equals(userType))
            return null;
        if (userId == null) return null;
        try {
            return CartDAO.getCartForUser(userId);
        } catch (Exception e) {
            System.err.println("Error getting user cart: " + e.getMessage());
            return null;
        }
    }

}