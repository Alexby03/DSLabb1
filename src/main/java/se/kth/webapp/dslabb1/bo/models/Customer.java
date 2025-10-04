package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.UserType;

import java.util.UUID;

/**
 * Class representing the customer class and object.
 * Customer implements IUser.
 */
public class Customer implements IUser {
    private final UUID customerId;
    private final String email;
    private final String fullName;
    private final UserType userType;
    private final Boolean isActive;
    private String address;
    private String paymentMethod;

    /**
     * Reconstructs a customer from the database.
     *
     * @param customerId    unique ID of the customer.
     * @param email         of the customer.
     * @param address       of the customer.
     * @param fullName      of the customer.
     * @param paymentMethod of the customer.
     * @param isActive      whether the customer has ended their account or not.
     */
    public Customer(UUID customerId, String email, String address, String fullName, String paymentMethod, Boolean isActive) {
        this.customerId = customerId;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.userType = UserType.CUSTOMER;
        this.paymentMethod = paymentMethod;
        this.isActive = isActive;
    }

    /**
     * Generates a new instance of Customer.
     *
     * @param email    of the new customer.
     * @param address  of the new customer.
     * @param fullName of the new customer.
     */
    public Customer(String email, String address, String fullName) {
        this.customerId = UUID.randomUUID();
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.userType = UserType.CUSTOMER;
        this.isActive = true;
        this.paymentMethod = null;
    }

    @Override
    public UUID getId() {
        return customerId;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public UserType getUserType() {
        return this.userType;
    }

    @Override
    public Boolean isActive() {
        return isActive;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}

