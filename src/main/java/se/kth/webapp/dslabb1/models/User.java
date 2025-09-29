package se.kth.webapp.dslabb1.models;

import java.util.UUID;

public interface User {
    UUID getId();
    String getEmail();
    String getPassword();
    String getFullName();

    enum UserType {
        CUSTOMER,
        ADMIN,
        WAREHOUSEWORKER
    }

    UserType getUserType();
}
