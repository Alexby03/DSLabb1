package se.kth.webapp.dslabb1.bo.services;

import se.kth.webapp.dslabb1.bo.models.Admin;
import se.kth.webapp.dslabb1.bo.models.Customer;
import se.kth.webapp.dslabb1.bo.models.IUser;
import se.kth.webapp.dslabb1.bo.models.Worker;
import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;
import se.kth.webapp.dslabb1.db.data.UserDAO;

public class UserService {

    /**
     * Authenticates a user against the database directory.
     * @param email of the customer
     * @param password of the customer
     * @return a customer if succeeded login, else null.
     */
    public static IUser authenticateUser(String email, String password) {
        UserDAO user = UserDAO.findByEmail(email);
        if (user == null) {
            return null;
        }
        if (user.userPassword().equals(password)) {
            switch (user.userType()) {
                case UserType.CUSTOMER -> {
                    return new Customer(
                            user.userId(), user.email(), user.address(),
                            user.fullName(), user.paymentMethod(),user.isActive()
                    );
                }
                case UserType.ADMIN -> {
                    return new Admin(
                            user.userId(), user.email(), user.address(),
                            user.fullName(), user.isActive()
                    );
                }
                case UserType.WAREHOUSEWORKER -> {
                    return new Worker(
                            user.userId(), user.email(), user.address(),
                            user.fullName(), user.isActive()
                    );
                }
            }
        }
        return null;
    }


    public static Result registerUser(String fullName, String email, String address, String userPassword){

        Customer user = new Customer(email, address, fullName);

        return UserDAO.createUser(user, userPassword);
    }

    public static Result updateCustomer(String newEmail, String newFullName, String newAddress, String userPassword, Customer customer, String newPaymentMethod) {

        if (customer == null) {
            return Result.FAILED;
        }

        // Handle null password - keep existing if not provided
        if (userPassword == null || userPassword.isBlank()) {
            UserDAO existingData = UserDAO.findById(customer.getId());
            userPassword = existingData != null ? existingData.userPassword() : "";
        }

        // Use existing values if new ones are null/empty (optional - depends on your preference)
        String email = (newEmail != null && !newEmail.isBlank()) ? newEmail : customer.getEmail();
        String fullName = (newFullName != null && !newFullName.isBlank()) ? newFullName : customer.getFullName();
        String address = (newAddress != null) ? newAddress : customer.getAddress();
        String paymentMethod = (newPaymentMethod != null) ? newPaymentMethod : customer.getPaymentMethod();

        Customer user = new Customer(customer.getId(), email, address, fullName, paymentMethod, customer.isActive());
        return UserDAO.updateUser(user, userPassword);
    }

}
