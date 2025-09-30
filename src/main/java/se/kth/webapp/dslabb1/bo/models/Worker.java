package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;

import java.util.UUID;

public class Worker implements IUser {
    private final UUID workerId;
    private String email;
    private String passwordHash;
    private String fullName;
    private Boolean isActive;

    public Worker(UUID id, String email, String passwordHash, String fullName,  Boolean isActive) {
        this.workerId = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.isActive = isActive;
    }

    @Override public UUID getId() { return workerId; }
    @Override public String getEmail() { return email; }
    @Override public String getPasswordHash() { return passwordHash; }
    @Override public String getFullName() { return fullName; }
    @Override public UserType getUserType() { return UserType.WAREHOUSEWORKER; }

    @Override
    public Boolean isActive() {return isActive;}

    @Override
    public Boolean isInactive() {
        return !isActive;
    }
}
