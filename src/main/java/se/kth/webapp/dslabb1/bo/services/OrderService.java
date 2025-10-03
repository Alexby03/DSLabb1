package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.Cart;
import se.kth.webapp.dslabb1.bo.models.CartItem;
import se.kth.webapp.dslabb1.bo.models.Item;
import  se.kth.webapp.dslabb1.bo.models.Order;
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
import java.util.*;

public class OrderService {

    public static Result createOrderFromCart(UUID userId, Cart cart) {
        try (Connection conn = DBManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Order order = new Order(userId, cart.getItems());
                Result orderResult = OrderDAO.createOrder(conn, order);

                for (CartItem cartItem : cart.getItems()) {

                    Item item = new Item(order.getOrderId(), cartItem.getSku(), cartItem.getProductName(),
                            cartItem.getPrice(),  cartItem.getQuantity());
                    Result itemResult = ItemDAO.createItem(conn, item);
                    if (itemResult != Result.SUCCESS) {
                        conn.rollback();
                        return Result.FAILED;
                    }

                    String productSKU =  cartItem.getSku();
                    int currenStockDiff = ProductDAO.findBySku(productSKU).quantity()-cartItem.getQuantity();
                    if(currenStockDiff < 0) return Result.FAILED;
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
            System.err.println("Error creating Order: "+e.getMessage());
            return Result.FAILED;
        }

    }

    public static Result cancelOrder(UUID userId, UUID orderId, UserType userType) {
        if (userId == null || orderId == null) {
            return Result.FAILED;
        }

        try (Connection conn = DBManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Get current order to check ownership and status
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

    public static Result updateOrderStatus(UUID orderId, OrderStatus newStatus, UserType  userType) {
        if(!UserType.ADMIN.equals(userType) && !UserType.WAREHOUSEWORKER.equals(userType)) return Result.PRIVILEGE;

        if(orderId == null || newStatus == null) {
            return Result.FAILED;
        }

        try(Connection conn = DBManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                OrderDAO currentOrder = OrderDAO.findById(orderId);
                if (currentOrder == null) {
                    conn.rollback();
                    return Result.FAILED;
                }
                if(currentOrder.orderStatus() == OrderStatus.CANCELED){
                    conn.rollback();
                    return Result.FAILED;
                }

                OrderDAO.updateOrderStatus(orderId, newStatus);

                conn.commit();
                return Result.SUCCESS;
            } catch (Exception e) {
                conn.rollback();
                return  Result.FAILED;
            }
        } catch (SQLException e) {
           System.err.println("Error Updating order status: "+e.getMessage());
           return Result.FAILED;
        }
    }

    public static Order getOrderById(UUID orderId, UUID requestingUserId, UserType userType) {
        if (orderId == null || requestingUserId == null) {
            return null;
        }

        try {
            OrderDAO orderDao = OrderDAO.findById(orderId);
            if (orderDao == null) {
                return null;
            }

            if (UserType.CUSTOMER.equals(userType) && !orderDao.userId().equals(requestingUserId)) {
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

    public static double calculateOrderTotal(UUID orderId, UserType userType) {
        if (orderId == null) {
            return 0.0;
        }

        try {
            List<Item> items = ItemDAO.findItemsWithProductDetails(orderId);
            double total = 0.0;

            for (Item item : items) {
                total += item.getUnitPrice() * item.getQuantity();
            }
            return total;

        } catch (Exception e) {
            System.err.println("Error calculating order total: " + e.getMessage());
            return 0.0;
        }
    }

    public static int getOrderItemCount(UUID orderId, UserType userType) {
        if (orderId == null) {
            return 0;
        }

        try {
            List<Item> items = ItemDAO.findItemsWithProductDetails(orderId);
            int count = 0;

            for (Item item : items) {
                count += item.getQuantity();
            }

            return count;

        } catch (Exception e) {
            System.err.println("Error counting order items: " + e.getMessage());
            return 0;
        }
    }
}
