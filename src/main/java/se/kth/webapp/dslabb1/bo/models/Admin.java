package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.UserType;

import java.util.UUID;

/**
 * Class representing the Admin class and object.
 * Admin implements IUser.
 */
public class Admin implements IUser {

    private final UUID adminId;
    private final String email;
    private final String fullName;
    private final Boolean isActive;

    /**
     * Reconstructs an admin from the database.
     *
     * @param adminId  unique ID of the admin.
     * @param email    of the admin.
     * @param fullName of the admin.
     * @param isActive whether the admin account is active or not.
     */
    public Admin(UUID adminId, String email, String fullName, Boolean isActive) {
        this.adminId = adminId;
        this.email = email;
        this.fullName = fullName;
        this.isActive = isActive;
    }

    @Override
    public UUID getId() {
        return adminId;
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
        return UserType.ADMIN;
    }

    @Override
    public Boolean isActive() {
        return isActive;
    }

}
