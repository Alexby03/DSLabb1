package se.kth.webapp.dslabb1.bo.models;

import java.util.UUID;

import se.kth.webapp.dslabb1.bo.models.enums.Result;
import se.kth.webapp.dslabb1.bo.models.enums.UserType;

public interface IUser {
    UUID getId();
    String getEmail();
    String getPasswordHash();
    String getFullName();
    UserType getUserType();
    Boolean isActive();
    Boolean isInactive();
}
