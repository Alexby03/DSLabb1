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
    private final String passwordHash;
    private final String fullName;
    private String address;
    private Boolean isActive;

    public Customer(UUID customerId ,String email, String passwordHash, String address,String fullName, Boolean isActive) {
        this.customerId = customerId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.address = address;
        this.isActive = isActive;
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
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public UserType getUserType() {
        return UserType.CUSTOMER;
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

}

