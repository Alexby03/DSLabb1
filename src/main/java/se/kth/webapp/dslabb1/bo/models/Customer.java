package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;

import java.util.UUID;

/**
 * Customer implements IUser. orders are fetched from DB when needed.
 */
public class Customer implements IUser {
    private final UUID customerId;
    private final String email;
    private final String fullName;
    private String address;
    private UserType userType;
    private String paymentMethod;
    private Boolean isActive;

    public Customer(UUID customerId ,String email, String address,String fullName, String paymentMethod, Boolean isActive) {
        this.customerId = customerId;
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.userType = UserType.CUSTOMER;
        this.paymentMethod = paymentMethod;
        this.isActive = isActive;
    }

    public Customer(String email, String address,String fullName) {
        this.customerId = UUID.randomUUID();
        this.email = email;
        this.fullName = fullName;
        this.address = address;
        this.userType = UserType.CUSTOMER;
        this.isActive = true;
        this.paymentMethod = null;
    }

    // NOTE: do NOT store List<Order> here by default, use DAO to fetch orders


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
    public Boolean isActive() {return isActive;}

    @Override
    public Boolean isInactive(){
        return !isActive;
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

