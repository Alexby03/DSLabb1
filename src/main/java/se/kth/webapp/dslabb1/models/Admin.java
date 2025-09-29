package se.kth.webapp.dslabb1.models;

import java.util.UUID;

public class Admin implements User{

    private final UUID adminId;
    private String email;
    private String passwordHash;
    private String fullName;

    public Admin(UUID adminId, String email, String passwordHash, String fullName) {
        this.adminId = adminId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    @Override
    public UUID getId() {return adminId;}

    @Override
    public String getEmail() { return email; }

    @Override
    public String getPasswordHash() { return passwordHash; }

    @Override
    public String getFullName() { return fullName; }

    @Override
    public UserType getUserType() { return UserType.ADMIN; }
}
