package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.Cart;
import se.kth.webapp.dslabb1.bo.models.CartItem;
import se.kth.webapp.dslabb1.bo.models.Item;
import se.kth.webapp.dslabb1.bo.models.Order;
import se.kth.webapp.dslabb1.bo.models.enums.OrderStatus;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.DBManager;
import se.kth.webapp.dslabb1.db.data.CartDAO;
import se.kth.webapp.dslabb1.db.data.ItemDAO;
import se.kth.webapp.dslabb1.db.data.OrderDAO;
import se.kth.webapp.dslabb1.db.data.ProductDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service class providing methods for handling orders to the presentation layer.
 */
public class OrderService {

    /**
     * Attempts to generate a new order to the database by using a cart.
     *
     * @param userId the id of the customer who made the order.
     * @param cart   the customer's cart instance.
     * @return whether generating the order was successful or not.
     */
    public static Result createOrderFromCart(UUID userId, Cart cart) {
        try (Connection conn = DBManager.getConnection()) {

            conn.setAutoCommit(false);

            try {

                Order order = new Order(userId, cart.items());
                Result orderResult = OrderDAO.createOrder(conn, order);

                if (orderResult == Result.FAILED) {

                    conn.rollback();
                    return Result.FAILED;
                }

                for (CartItem cartItem : cart.items()) {

                    Item item = new Item(order.getOrderId(), cartItem.getSku(), cartItem.getProductName(),
                            cartItem.getPrice(), cartItem.getQuantity());
                    Result itemResult = ItemDAO.createItem(conn, item);
                    if (itemResult != Result.SUCCESS) {
                        conn.rollback();
                        return Result.FAILED;
                    }

                    String productSKU = cartItem.getSku();
                    int currenStockDiff = ProductDAO.findBySku(productSKU).quantity() - cartItem.getQuantity();
                    if (currenStockDiff < 0) return Result.FAILED;
                    ProductDAO.updateStock(productSKU, currenStockDiff, conn);

                }

                CartDAO.clearCart(userId, conn);
                conn.commit();
                return Result.SUCCESS;

            } catch (SQLException e) {

                conn.rollback();
                return Result.FAILED;

            }
        } catch (SQLException e) {

            System.err.println("Error creating Order: " + e.getMessage());
            return Result.FAILED;

        }

    }

    /**
     * Cancels an order.
     *
     * @param userId   the id of the customer owning the order.
     * @param orderId  of the order.
     * @param userType if the user is actually a customer.
     * @return whether updating cancelling the order was successful or not.
     */
    public static Result cancelOrder(UUID userId, UUID orderId, UserType userType) {
        if (userId == null || orderId == null) {

            return Result.FAILED;

        }

        try (Connection conn = DBManager.getConnection()) {

            conn.setAutoCommit(false);

            try {

                OrderDAO currentOrder = OrderDAO.findById(orderId);
                if (currentOrder == null) {

                    conn.rollback();
                    return Result.FAILED;

                }

                // Auth check if customer owns order or is admin

                if (UserType.CUSTOMER.equals(userType) && !currentOrder.userId().equals(userId)) {

                    conn.rollback();
                    return Result.FAILED;

                }

                if (!UserType.ADMIN.equals(userType) && !UserType.CUSTOMER.equals(userType)) {

                    conn.rollback();
                    return Result.PRIVILEGE;

                }

                if (currentOrder.orderStatus() != OrderStatus.PAID) {

                    conn.rollback();
                    return Result.FAILED;

                }

                OrderDAO.updateOrderStatus(orderId, OrderStatus.CANCELED);

                List<ItemDAO> orderItems = ItemDAO.findByOrderId(orderId);
                for (ItemDAO itemDAO : orderItems) {
                    ProductDAO product = ProductDAO.findBySku(itemDAO.sku());
                    if (product != null) {
                        int newStock = product.quantity() + itemDAO.quantity();
                        Result stockUpdate = ProductDAO.updateStock(itemDAO.sku(), newStock, conn);
                        if (stockUpdate != Result.SUCCESS) {
                            conn.rollback();
                            return Result.FAILED;
                        }
                    }
                }

                conn.commit();
                return Result.SUCCESS;

            } catch (SQLException e) {

                conn.rollback();
                return Result.FAILED;

            }
        } catch (SQLException e) {

            System.err.println("Error canceling order: " + e.getMessage());
            return Result.FAILED;

        }
    }

    /**
     * Updates the status of the order.
     *
     * @param orderId   of the order.
     * @param newStatus of the order.
     * @param userType  if the user is actually a customer.
     * @return whether updating the status was successful or not.
     */
    public static Result updateOrderStatus(UUID orderId, OrderStatus newStatus, UserType userType) {
        if (!UserType.ADMIN.equals(userType) && !UserType.WAREHOUSEWORKER.equals(userType)) return Result.PRIVILEGE;

        if (orderId == null || newStatus == null) {
            return Result.FAILED;
        }

        try (Connection conn = DBManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                OrderDAO currentOrder = OrderDAO.findById(orderId);
                if (currentOrder == null) {
                    conn.rollback();
                    return Result.FAILED;
                }
                if (currentOrder.orderStatus() == OrderStatus.CANCELED) {
                    conn.rollback();
                    return Result.FAILED;
                }

                OrderDAO.updateOrderStatus(orderId, newStatus);

                conn.commit();
                return Result.SUCCESS;
            } catch (Exception e) {
                conn.rollback();
                return Result.FAILED;
            }
        } catch (SQLException e) {
            System.err.println("Error Updating order status: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Attempts to retrieve an order through the orderId.
     *
     * @param orderId  the id of the order.
     * @param userId   the id of the customer owning the order.
     * @param userType if the user is actually a customer or not.
     * @return the order if successful, otherwise null.
     */
    public static Order getOrderById(UUID orderId, UUID userId, UserType userType) {
        if (orderId == null || userId == null) {
            return null;
        }

        try {
            OrderDAO orderDao = OrderDAO.findById(orderId);
            if (orderDao == null) {
                return null;
            }

            //ownership check
            if (UserType.CUSTOMER.equals(userType) && !orderDao.userId().equals(userId)) {
                return null;
            }
            if (!UserType.CUSTOMER.equals(userType) && !UserType.ADMIN.equals(userType) && !UserType.WAREHOUSEWORKER.equals(userType)) {
                return null;
            }

            List<Item> items = ItemDAO.findItemsWithProductDetails(orderDao.orderId());

            return new Order(orderDao.orderId(), orderDao.userId(), items,
                    orderDao.dateOfPurchase().atStartOfDay(), orderDao.orderStatus());

        } catch (Exception e) {
            System.err.println("Error getting order by ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Attempts to retrieve all the orders of a particular customer.
     *
     * @param customerId the id of the customer.
     * @param userType   if the user is actually a customer or not.
     * @return the list of orders if successful, otherwise empty list.
     */
    public static List<Order> getCustomerOrders(UUID customerId, UserType userType) {

        if (!UserType.CUSTOMER.equals(userType) && !UserType.ADMIN.equals(userType)) {
            return new ArrayList<>();
        }
        if (customerId == null) {
            return new ArrayList<>();
        }

        try {
            List<OrderDAO> orderDaos = OrderDAO.findByUserId(customerId);
            List<Order> orders = new ArrayList<>();

            for (OrderDAO orderDao : orderDaos) {
                List<Item> items = ItemDAO.findItemsWithProductDetails(orderDao.orderId());
                orders.add(new Order(orderDao.orderId(), orderDao.userId(), items,
                        orderDao.dateOfPurchase().atStartOfDay(), orderDao.orderStatus()));
            }

            return orders;

        } catch (Exception e) {
            System.err.println("Error getting customer orders history: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Calculates the total cost of the order.
     *
     * @param orderId id of the order.
     * @return the total cost of the order.
     */
    public static double calculateOrderTotal(UUID orderId) {
        if (orderId == null) {
            return 0.0;
        }

        try {
            List<Item> items = ItemDAO.findItemsWithProductDetails(orderId);
            double total = 0.0;

            for (Item item : items) {
                total += item.unitPrice() * item.quantity();
            }
            return total;

        } catch (Exception e) {
            System.err.println("Error calculating order total: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Retrieves the total amount of products within all items in the order.
     *
     * @param orderId id of the order.
     * @return the total count of products within the order.
     */
    public static int getOrderItemCount(UUID orderId) {
        if (orderId == null) {
            return 0;
        }

        try {
            List<Item> items = ItemDAO.findItemsWithProductDetails(orderId);
            int count = 0;

            for (Item item : items) {
                count += item.quantity();
            }

            return count;

        } catch (Exception e) {
            System.err.println("Error counting order items: " + e.getMessage());
            return 0;
        }
    }
}
