package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.*;
import se.kth.webapp.dslabb1.bo.models.enums.OrderStatus;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.db.DBManager;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public record OrderDAO(
        UUID orderId,
        UUID userId,
        double totalAmount,
        LocalDate dateOfPurchase,
        OrderStatus orderStatus
) {

    /**
     * Create order, transaction and exceptions handled outside method
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
     * Find order by ID
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
     * Find orders by user ID
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
     * Get all orders
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
     * Update order status
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
     * Convert OrderDAO to domain model Order
     * Note: Items must be loaded separately via ItemDAO
     */
    public Order toDomainModel() {
        // Create empty items list - will be populated by service layer
        List<Item> items = new ArrayList<>();
        LocalDateTime dateTime = this.dateOfPurchase.atStartOfDay();

        return new Order(this.orderId, this.userId, items, dateTime, this.orderStatus);
    }

    /**
     * Create OrderDAO from domain model Order
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
}

