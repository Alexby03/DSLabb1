package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.Customer;
import se.kth.webapp.dslabb1.bo.models.IUser;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.data.UserDAO;
import se.kth.webapp.dslabb1.ui.info.UserInfo;

import java.util.UUID;

/**
 * Service class providing methods for handling users to the presentation layer.
 */
public class UserService {

    /**
     * Authenticates a user against the database directory.
     *
     * @param email    of the customer
     * @param password of the customer
     * @return a customer if succeeded login, else null.
     */
    public static UserInfo authenticateUser(String email, String password) {
        UserDAO user = UserDAO.findByEmail(email);
        if (user == null) {
            return null;
        }
        if (user.userPassword().equals(password)) {
            switch (user.userType()) {
                case UserType.CUSTOMER -> {
                    return new UserInfo(
                            user.userId(), user.email(), user.fullName(),
                            user.userType(), user.isActive(), user.address(), user.paymentMethod()
                    );
                }
                default -> { //for admin and warehouseworker
                    return new UserInfo(
                            user.userId(), user.email(), user.fullName(),
                            user.userType(), user.isActive(), null, null
                    );
                }
            }

        }
        return null;
    }

    /**
     * Generates a new user to the database.
     *
     * @param fullName     the name of the registered user.
     * @param email        the email of the registered user.
     * @param address      the address of the registered user.
     * @param userPassword the password of the registered user.
     * @return whether creating a user was successful or not.
     */
    public static Result registerUser(String fullName, String email, String address, String userPassword) {

        Customer user = new Customer(email, address, fullName);

        return UserDAO.createUser(user, userPassword);
    }

    /**
     * Updates a user within the database.
     *
     * @param newEmail
     * @param newFullName
     * @param newAddress
     * @param userPassword
     * @param customer         the old instance of the customer.
     * @param newPaymentMethod
     * @return whether updating the customer was successful or not.
     */
    public static Result updateCustomer(String newEmail, String newFullName, String newAddress, String userPassword, UserInfo customer, String newPaymentMethod) {

        if (customer == null) {
            return Result.FAILED;
        }

        if (userPassword == null || userPassword.isBlank()) {
            UserDAO existingData = UserDAO.findById(customer.userId());
            userPassword = existingData != null ? existingData.userPassword() : "";
        }

        String email = (newEmail != null && !newEmail.isBlank()) ? newEmail : customer.email();
        String fullName = (newFullName != null && !newFullName.isBlank()) ? newFullName : customer.fullName();
        String address = (newAddress != null) ? newAddress : customer.address();
        String paymentMethod = (newPaymentMethod != null) ? newPaymentMethod : customer.paymentMethod();

        Customer user = new Customer(customer.userId(), email, address, fullName, paymentMethod, customer.isActive());
        return UserDAO.updateUser(user, userPassword);
    }

    public static UserInfo findById(UUID userId) {
        UserDAO foundUser = UserDAO.findById(userId);
        if (foundUser == null) {
            return null;
        }
        return new UserInfo(foundUser.userId(), foundUser.email(), foundUser.fullName(),
                foundUser.userType(), foundUser.isActive(), foundUser.address(), foundUser.paymentMethod());
    }

}
