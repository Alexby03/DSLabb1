package se.kth.webapp.dslabb1.bo.models;

import java.util.UUID;

public interface IUser {
    UUID getId();
    String getEmail();
    String getPasswordHash();
    String getFullName();
    UserType getUserType();
}
