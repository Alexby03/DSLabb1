package se.kth.webapp.dslabb1.models;

import java.util.UUID;

/**
 * Customer implements User. orders are fetched from DB when needed.
 */
public class Customer implements User {
    private final UUID customerId;
    private final String email;
    private final String passwordHash;
    private final String fullName;
    private String address;

    public Customer(UUID customerId ,String email, String passwordHash, String fullName) {
        this.customerId = customerId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

