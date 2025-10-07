package se.kth.webapp.dslabb1.db.data;

import se.kth.webapp.dslabb1.bo.models.Admin;
import se.kth.webapp.dslabb1.bo.models.Customer;
import se.kth.webapp.dslabb1.bo.models.IUser;
import se.kth.webapp.dslabb1.bo.models.Worker;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.DBManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data access object for an entity in table T_User in DB schema.
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
    /**
     * Generates a new user to the database.
     *
     * @param user         instance of a User implementing IUser.
     * @param userPassword the password of the registered user.
     * @return whether creating a user was successful or not.
     */
    public static Result createUser(IUser user, String userPassword) {
        String sql = "INSERT INTO T_User (userId, email, userPassword, address, fullName, paymentMethod, userType, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        UserDAO userDao = fromDomainModel(user, userPassword);
        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {

            stmt.setString(1, userDao.userId.toString());
            stmt.setString(2, userDao.email);
            stmt.setString(3, userDao.userPassword);
            stmt.setString(4, userDao.address);
            stmt.setString(5, userDao.fullName);
            stmt.setString(6, userDao.paymentMethod);
            stmt.setString(7, userDao.userType.name());
            stmt.setBoolean(8, userDao.isActive);

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Finds a user in the database and returns it.
     *
     * @param userId the queried ID of the user.
     * @return the user data access object if found, otherwise null.
     */
    public static UserDAO findById(UUID userId) {
        String sql = "SELECT * FROM T_User WHERE userId = ?";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {

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
     * Finds a user in the database and returns it.
     *
     * @param email the queried email of the user.
     * @return the user data access object if found, otherwise null.
     */
    public static UserDAO findByEmail(String email) {
        String sql = "SELECT * FROM T_User WHERE email = ?";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {

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
     * Attempts to find all users currently existing.
     *
     * @return list of user data access objects of found users.
     */
    public static List<UserDAO> findAll() {
        List<UserDAO> users = new ArrayList<>();
        String sql = "SELECT * FROM T_User";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql);
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
     * Updates a user within the database.
     *
     * @param user         instance of a User implementing IUser.
     * @param userPassword the password of the registered user.
     * @return whether updating a user was successful or not.
     */
    public static Result updateUser(IUser user, String userPassword) {
        String sql = "UPDATE T_User SET email = ?, userPassword = ?, address = ?, fullName = ?, paymentMethod = ? WHERE userId = ?";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {

            UserDAO userDao = fromDomainModel(user, userPassword);

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
     * Marks the user as inactive.
     *
     * @param userId of the user.
     * @return whether inactivating the user was successful or not.
     */
    public static Result deactivateUser(UUID userId) {
        String sql = "UPDATE T_User SET isActive = FALSE WHERE userId = ?";

        try (DBManager db = DBManager.open();
             PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {

            stmt.setString(1, userId.toString());

            return stmt.executeUpdate() > 0 ? Result.SUCCESS : Result.FAILED;

        } catch (SQLException e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return Result.FAILED;
        }
    }

    /**
     * Converts the user instance implementing IUser to a user data access object.
     *
     * @return the user data access object.
     */
    public static UserDAO fromDomainModel(IUser user, String userPassword) {
        String address = (user instanceof Customer customer) ? customer.getAddress() : null;
        String paymentMethod = (user instanceof Customer customer) ? customer.getPaymentMethod() : null;

        return new UserDAO(
                user.getId(),
                user.getEmail(),
                userPassword,
                address,
                user.getFullName(),
                paymentMethod,
                user.getUserType(),
                user.isActive()
        );
    }

    /**
     * Converts the user data access object to a user instance implementing IUser.
     *
     * @return the user.
     */
    public IUser toDomainModel() {
        return switch (this.userType) {
            case CUSTOMER -> {
                Customer customer = new Customer(this.userId, this.email, this.address, this.fullName, this.paymentMethod, this.isActive);
                if (this.address != null) {
                    customer.setAddress(this.address);
                }
                yield customer;
            }
            case ADMIN -> new Admin(this.userId, this.email, this.fullName, this.isActive);
            case WAREHOUSEWORKER ->
                    new Worker(this.userId, this.email, this.userPassword, this.isActive);
        };
    }
}