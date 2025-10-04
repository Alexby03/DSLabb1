package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.Item;
import se.kth.webapp.dslabb1.bo.models.Order;
import se.kth.webapp.dslabb1.bo.models.enums.OrderStatus;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.db.DBManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data access object for an entity in table T_Order in DB schema.
 */
public record OrderDAO(
        UUID orderId,
        UUID userId,
        double totalAmount,
        LocalDate dateOfPurchase,
        OrderStatus orderStatus
) {

    /**
     * Generates a new order to the database.
     *
     * @param conn  the connection to the database.
     * @param order an order instance.
     * @return whether generating the order was successful or not.
     * @throws SQLException if creating the order failed on the database end.
     */
    public static Result createOrder(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO T_Order (orderId, userId, totalAmount, dateOfPurchase, orderStatus) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getOrderId().toString());
            ps.setString(2, order.getCustomerId().toString());
            ps.setBigDecimal(3, BigDecimal.valueOf(order.getTotalAmount()));
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(order.getDateOfPurchase()));
            ps.setString(5, order.getOrderStatus().name());
            return ps.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;
        }
    }

    /**
     * Finds an order in the database and returns it.
     *
     * @param orderId the id of the order.
     * @return the order data access object if found, otherwise null.
     */
    public static OrderDAO findById(UUID orderId) {
        String sql = "SELECT * FROM T_Order WHERE orderId = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new OrderDAO(
                            UUID.fromString(rs.getString("orderId")),
                            UUID.fromString(rs.getString("userId")),
                            rs.getDouble("totalAmount"),
                            rs.getDate("dateOfPurchase").toLocalDate(),
                            OrderStatus.valueOf(rs.getString("orderStatus"))
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding order by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds an order in the database and returns it.
     *
     * @param userId the id of the customer owning the order.
     * @return the order data access object if found, otherwise null.
     */
    public static List<OrderDAO> findByUserId(UUID userId) {
        List<OrderDAO> orders = new ArrayList<>();
        String sql = "SELECT * FROM T_Order WHERE userId = ? ORDER BY dateOfPurchase DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new OrderDAO(
                            UUID.fromString(rs.getString("orderId")),
                            UUID.fromString(rs.getString("userId")),
                            rs.getDouble("totalAmount"),
                            rs.getDate("dateOfPurchase").toLocalDate(),
                            OrderStatus.valueOf(rs.getString("orderStatus"))
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding orders by user: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Attempts to find all orders currently existing.
     *
     * @return list of order data access objects of found users.
     */
    public static List<OrderDAO> findAll() {
        List<OrderDAO> orders = new ArrayList<>();
        String sql = "SELECT * FROM T_Order ORDER BY dateOfPurchase DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(new OrderDAO(
                        UUID.fromString(rs.getString("orderId")),
                        UUID.fromString(rs.getString("userId")),
                        rs.getDouble("totalAmount"),
                        rs.getDate("dateOfPurchase").toLocalDate(),
                        OrderStatus.valueOf(rs.getString("orderStatus"))
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all orders: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Updates the status of the order.
     *
     * @param orderId   of the order.
     * @param newStatus of the order.
     * @return whether updating the status was successful or not.
     */
    public static Result updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        String sql = "UPDATE T_Order SET orderStatus = ? WHERE orderId = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setString(2, orderId.toString());

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     *
     * Converts an order instance to an order data access object.
     *
     * @param order the order instance.
     * @return the order data access object.
     */
    public static OrderDAO fromDomainModel(Order order) {
        return new OrderDAO(
                order.getOrderId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getDateOfPurchase().toLocalDate(),
                order.getOrderStatus()
        );
    }

    /**
     * Converts an order data access object to an order instance.
     *
     * @return the order instance.
     */
    public Order toDomainModel() {
        List<Item> items = new ArrayList<>();
        LocalDateTime dateTime = this.dateOfPurchase.atStartOfDay();

        return new Order(this.orderId, this.userId, items, dateTime, this.orderStatus);
    }
}

