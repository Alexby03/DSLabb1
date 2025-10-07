package se.kth.webapp.dslabb1.ui.info;

import se.kth.webapp.dslabb1.bo.models.enums.UserType;

import java.util.UUID;

public record UserInfo(UUID userId, String email, String fullName, UserType userType, Boolean isActive, String address, String paymentMethod) {

    public UserInfo{
        if(userId==null) throw new IllegalArgumentException("userId is null");
        if(email==null || email.isBlank()) throw new IllegalArgumentException("email is null");
    }
}
