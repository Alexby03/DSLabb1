package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.UserType;

import java.util.UUID;

/**
 * Class representing the Worker class and object.
 * Worker implements IUser.
 */
public class Worker implements IUser {
    private final UUID workerId;
    private final String email;
    private final String fullName;
    private final Boolean isActive;

    /**
     * Reconstructs a worker from the database.
     *
     * @param id       unique ID of the worker.
     * @param email    of the worker.
     * @param fullName of the worker.
     * @param isActive whether the worker's account is active or not.
     */
    public Worker(UUID id, String email, String fullName, Boolean isActive) {
        this.workerId = id;
        this.email = email;
        this.fullName = fullName;
        this.isActive = isActive;
    }

    @Override
    public UUID getId() {
        return workerId;
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
        return UserType.WAREHOUSEWORKER;
    }

    @Override
    public Boolean isActive() {
        return isActive;
    }

}
