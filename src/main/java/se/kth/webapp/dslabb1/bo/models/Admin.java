package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;

import java.util.UUID;

public class Admin implements IUser {

    private final UUID adminId;
    private String email;
    private String passwordHash;
    private String fullName;
    private Boolean isActive;

    public Admin(UUID adminId, String email, String passwordHash, String fullName, Boolean isActive) {
        this.adminId = adminId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.isActive = isActive;
    }

    @Override
    public UUID getId() {return adminId;}

    @Override
    public String getEmail() { return email; }

    @Override
    public String getFullName() { return fullName; }

    @Override
    public UserType getUserType() { return UserType.ADMIN; }

    @Override
    public Boolean isActive() {
        return isActive;
    }

    @Override
    public Boolean isInactive() {
        return !isActive;
    }
}
