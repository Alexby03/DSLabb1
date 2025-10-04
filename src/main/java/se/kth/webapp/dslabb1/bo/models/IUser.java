package se.kth.webapp.dslabb1.bo.models;

import se.kth.webapp.dslabb1.bo.models.enums.UserType;

import java.util.UUID;

/**
 * Interface representing a generic user in the database.
 */
public interface IUser {
    UUID getId();

    String getEmail();

    String getFullName();

    UserType getUserType();

    Boolean isActive();
}
