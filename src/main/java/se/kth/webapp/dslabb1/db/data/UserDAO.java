package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.Admin;
import se.kth.webapp.dslabb1.bo.models.Customer;
import se.kth.webapp.dslabb1.bo.models.IUser;
import se.kth.webapp.dslabb1.bo.models.Worker;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.db.DBManager;

import java.sql.*;
import java.util.*;

/**
 * DAO record for User operations - matches T_User table structure
 */
public record UserDAO(
        UUID userId,
        String email,
        String userPassword,
        String address,
        String fullName,
        String paymentMethod,
        UserType userType,
        Boolean isActive
) {

    // Static methods for database operations

    /**
     * Create a new user in the database
     */
    public static Result createUser(UserDAO userDao) {
        String sql = "INSERT INTO T_User (userId, email, userPassword, address, fullName, paymentMethod, userType, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userDao.userId.toString());
            stmt.setString(2, userDao.email);
            stmt.setString(3, userDao.userPassword);
            stmt.setString(4, userDao.address);
            stmt.setString(5, userDao.fullName);
            stmt.setString(6, userDao.paymentMethod);
            stmt.setString(7, userDao.userType.name());

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Find user by ID
     */
    public static UserDAO findById(UUID userId) {
        String sql = "SELECT * FROM T_User WHERE userId = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserDAO(
                            UUID.fromString(rs.getString("userId")),
                            rs.getString("email"),
                            rs.getString("userPassword"),
                            rs.getString("address"),
                            rs.getString("fullName"),
                            rs.getString("paymentMethod"),
                            UserType.valueOf(rs.getString("userType")),
                            rs.getBoolean("isActive")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Find user by email
     */
    public static UserDAO findByEmail(String email) {
        String sql = "SELECT * FROM T_User WHERE email = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserDAO(
                            UUID.fromString(rs.getString("userId")),
                            rs.getString("email"),
                            rs.getString("userPassword"),
                            rs.getString("address"),
                            rs.getString("fullName"),
                            rs.getString("paymentMethod"),
                            UserType.valueOf(rs.getString("userType")),
                            rs.getBoolean("isActive")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all users
     */
    public static List<UserDAO> findAll() {
        List<UserDAO> users = new ArrayList<>();
        String sql = "SELECT * FROM T_User";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new UserDAO(
                        UUID.fromString(rs.getString("userId")),
                        rs.getString("email"),
                        rs.getString("userPassword"),
                        rs.getString("address"),
                        rs.getString("fullName"),
                        rs.getString("paymentMethod"),
                        UserType.valueOf(rs.getString("userType")),
                        rs.getBoolean("isActive")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Update user
     */
    public static Result updateUser(UserDAO userDao) {
        String sql = "UPDATE T_User SET email = ?, userPassword = ?, address = ?, fullName = ?, paymentMethod = ? WHERE userId = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {


            stmt.setString(1, userDao.email);
            stmt.setString(2, userDao.userPassword);
            stmt.setString(3, userDao.address);
            stmt.setString(4, userDao.fullName);
            stmt.setString(5, userDao.paymentMethod);
            stmt.setString(6, userDao.userId.toString());

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Mark user as inactive. We want to have recordkeeping!
     */
    public static Result deactivateUser(UUID userId) {
        String sql = "UPDATE T_User SET isActive = FALSE WHERE userId = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId.toString());

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Convert UserDAO to domain model User
     */
    public IUser toDomainModel() {
        return switch (this.userType) {
            case CUSTOMER -> {
                Customer customer = new Customer(this.userId, this.email, this.userPassword, this.address, this.fullName, this.isActive);
                if (this.address != null) {
                    customer.setAddress(this.address);
                }
                yield customer;
            }
            case ADMIN -> new Admin(this.userId, this.email, this.userPassword, this.fullName, this.isActive);
            case WAREHOUSEWORKER -> new Worker(this.userId, this.email, this.userPassword, this.fullName, this.isActive);
        };
    }

    /**
     * Create UserDAO from domain model User
     */
    public static UserDAO fromDomainModel(IUser user) {
        String address = (user instanceof Customer customer) ? customer.getAddress() : null;
        String paymentMethod = ""; // Can be expanded later

        return new UserDAO(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                address,
                user.getFullName(),
                paymentMethod,
                user.getUserType(),
                user.isActive()
        );
    }
}