package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.Cart;
import se.kth.webapp.dslabb1.bo.models.CartItem;
import se.kth.webapp.dslabb1.bo.models.Item;
import se.kth.webapp.dslabb1.bo.models.Order;
import se.kth.webapp.dslabb1.bo.models.enums.OrderStatus;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.DBManager;
import se.kth.webapp.dslabb1.db.DataAccessException;
import se.kth.webapp.dslabb1.db.data.CartDAO;
import se.kth.webapp.dslabb1.db.data.ItemDAO;
import se.kth.webapp.dslabb1.db.data.OrderDAO;
import se.kth.webapp.dslabb1.db.data.ProductDAO;
import se.kth.webapp.dslabb1.ui.info.ItemInfo;
import se.kth.webapp.dslabb1.ui.info.OrderInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class providing methods for handling orders to the presentation layer.
 */
public class OrderService {


    public static ItemInfo toItemInfo(Item item) {
        return new ItemInfo(item.orderId(), item.sku(), item.productName(), item.unitPrice(), item.quantity());
    }

    public static OrderInfo toOrderInfo(Order order) {
        if (order == null) return null;
        List<ItemInfo> itemInfoList = order.getItems().stream()
                .map(OrderService::toItemInfo)
                .collect(Collectors.toList());
        return new OrderInfo(order.getOrderId(), order.getCustomerId(), order.getDateOfPurchase(), itemInfoList, order.getOrderStatus(), order.getTotalAmount());
    }

    /**
     * Creates an order from the user's current cart.
     * This method fetches the cart internally and performs all business logic.
     *
     * @param userId the customer creating the order
     * @return Result indicating success or failure
     */
    public static Result createOrderFromCart(UUID userId) {

        try (DBManager db = DBManager.open()) {

            db.startTransaction();

            Cart cart = CartDAO.getCartForUser(userId);
            Order order = new Order(userId, cart.items());
            if (OrderDAO.createOrder(db.getConnection(), order) == Result.FAILED) {
                db.rollback();
                return Result.FAILED;
            }

            for (CartItem cartItem : cart.items()) {
                Item item = new Item(order.getOrderId(), cartItem.getSku(), cartItem.getProductName(),
                        cartItem.getPrice(), cartItem.getQuantity());

                if (ItemDAO.createItem(db.getConnection(), item) != Result.SUCCESS) {
                    db.rollback();
                    return Result.FAILED;
                }

                String sku = cartItem.getSku();
                int newStock = ProductDAO.findBySku(sku).quantity() - cartItem.getQuantity();
                if (newStock < 0) {
                    db.rollback();
                    return Result.FAILED;
                }

                ProductDAO.updateStock(sku, newStock, db.getConnection());
            }

            CartDAO.clearCart(userId, db.getConnection());
            db.commit();
            return Result.SUCCESS;

        } catch (DataAccessException e) {
            System.err.println("Order transaction failed: " + e.getMessage());
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

        try (DBManager db = DBManager.open()) {

            db.startTransaction();


            OrderDAO currentOrder = OrderDAO.findById(orderId);
            if (currentOrder == null) {

                db.rollback();
                return Result.FAILED;

            }

            // Auth check if customer owns order or is admin

            if (UserType.CUSTOMER.equals(userType) && !currentOrder.userId().equals(userId)) {

                db.rollback();
                return Result.FAILED;

            }

            if (!UserType.ADMIN.equals(userType) && !UserType.CUSTOMER.equals(userType)) {

                db.rollback();
                return Result.PRIVILEGE;

            }

            if (currentOrder.orderStatus() != OrderStatus.PAID) {

                db.rollback();
                return Result.FAILED;

            }

            OrderDAO.updateOrderStatus(orderId, OrderStatus.CANCELED);

            List<ItemDAO> orderItems = ItemDAO.findByOrderId(orderId);
            for (ItemDAO itemDAO : orderItems) {
                ProductDAO product = ProductDAO.findBySku(itemDAO.sku());
                if (product != null) {
                    int newStock = product.quantity() + itemDAO.quantity();
                    Result stockUpdate = ProductDAO.updateStock(itemDAO.sku(), newStock, db.getConnection());
                    if (stockUpdate != Result.SUCCESS) {
                        db.rollback();
                        return Result.FAILED;
                    }
                }
            }

            db.commit();
            return Result.SUCCESS;


        } catch (DataAccessException e) {

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

        try (DBManager db = DBManager.open()) {
            db.startTransaction();

            OrderDAO currentOrder = OrderDAO.findById(orderId);
            if (currentOrder == null) {
                db.rollback();
                return Result.FAILED;
            }
            if (currentOrder.orderStatus() == OrderStatus.CANCELED) {
                db.rollback();
                return Result.FAILED;
            }

            OrderDAO.updateOrderStatus(orderId, newStatus);

            db.commit();
            return Result.SUCCESS;

        } catch (DataAccessException e) {
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
    public static OrderInfo getOrderById(UUID orderId, UUID userId, UserType userType) {
        if (orderId == null || userId == null)
            return null;
        try {
            OrderDAO orderDao = OrderDAO.findById(orderId);
            if (orderDao == null) return null;

            // ownership check
            if (UserType.CUSTOMER.equals(userType) && !orderDao.userId().equals(userId))
                return null;
            if (!UserType.CUSTOMER.equals(userType) && !UserType.ADMIN.equals(userType) && !UserType.WAREHOUSEWORKER.equals(userType))
                return null;

            List<Item> items = ItemDAO.findItemsWithProductDetails(orderDao.orderId());
            Order order = new Order(orderDao.orderId(), orderDao.userId(), items,
                    orderDao.dateOfPurchase().atStartOfDay(), orderDao.orderStatus()
            );

            return toOrderInfo(order);
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
    public static List<OrderInfo> getCustomerOrders(UUID customerId, UserType userType) {
        if (!UserType.CUSTOMER.equals(userType) && !UserType.ADMIN.equals(userType)) return new ArrayList<>();
        if (customerId == null) return new ArrayList<>();

        try {
            List<OrderDAO> orderDaos = OrderDAO.findByUserId(customerId);
            List<OrderInfo> orders = new ArrayList<>();
            for (OrderDAO orderDao : orderDaos) {
                List<Item> items = ItemDAO.findItemsWithProductDetails(orderDao.orderId());
                Order order = new Order(orderDao.orderId(), orderDao.userId(), items, orderDao.dateOfPurchase().atStartOfDay(),
                        orderDao.orderStatus());
                orders.add(toOrderInfo(order));
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

    public static List<OrderInfo> findAll() {

        List<OrderDAO> foundOrders = OrderDAO.findAll();
        return foundOrders.stream()
                .map(OrderDAO::toDomainModel)
                .map(OrderService::toOrderInfo)
                .collect(Collectors.toList());

    }
}
