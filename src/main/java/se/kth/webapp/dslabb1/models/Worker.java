package se.kth.webapp.dslabb1.models;

import java.util.UUID;

public class Worker implements User{
    private final UUID workerId;
    private String email;
    private String passwordHash;
    private String fullName;

    public Worker(UUID id, String email, String passwordHash, String fullName) {
        this.workerId = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    @Override public UUID getId() { return workerId; }
    @Override public String getEmail() { return email; }
    @Override public String getPasswordHash() { return passwordHash; }
    @Override public String getFullName() { return fullName; }
    @Override public UserType getUserType() { return UserType.WAREHOUSEWORKER; }
}
