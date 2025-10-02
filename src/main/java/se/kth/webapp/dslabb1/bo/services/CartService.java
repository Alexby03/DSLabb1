package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.*;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.data.CartDAO;
import se.kth.webapp.dslabb1.db.data.ProductDAO;
import java.util.*;

public class CartService {

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


    public static Result clearCart(UUID userId, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType)) return Result.PRIVILEGE;

        if (userId == null) {
            return Result.FAILED;
        }

        try {
            return CartDAO.clearCart(userId);
        } catch (Exception e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return Result.FAILED;
        }
    }


    public static Cart getUserCart(UUID userId, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType) && !UserType.ADMIN.equals(userType)) {
            return null;
        }

        if (userId == null) {
            return null;
        }

        try {
            return CartDAO.getCartForUser(userId);
        } catch (Exception e) {
            System.err.println("Error getting user cart: " + e.getMessage());
            return null;
        }
    }


    public static double getCartTotal(UUID userId, UserType userType) {
        Cart cart = getUserCart(userId, userType);
        if (cart == null) {
            return 0.0;
        }

        try {
            double total = 0.0;
            List<CartItem> items = cart.getItems();
            for (CartItem item : items) {
                total += item.getPrice() * item.getQuantity();
            }

            return total;
        } catch (Exception e) {
            System.err.println("Error calculating cart total: " + e.getMessage());
            return 0.0;
        }
    }

    public static int getCartItemCount(UUID userId, UserType userType) {
        Cart cart = getUserCart(userId, userType);
        if (cart == null) {
            return 0;
        }

        try {
            int count = 0;
            List<CartItem> items = cart.getItems();
            for (CartItem item : items) {
                count += item.getQuantity();
            }

            return count;
        } catch (Exception e) {
            System.err.println("Error counting cart items: " + e.getMessage());
            return 0;
        }
    }

    public static List<CartItem> getAllItems(UUID userId, UserType userType){
        if (!UserType.CUSTOMER.equals(userType)) return null;
        return CartDAO.getCartForUser(userId).getItems();
    }

}