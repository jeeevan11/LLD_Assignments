package com.bookmyshow.model;

public class User {
    private final String userId;
    private final String name;
    private final String email;
    private final boolean isAdmin;

    public User(String userId, String name, String email, boolean isAdmin) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean isAdmin() { return isAdmin; }

    @Override
    public String toString() {
        return name + (isAdmin ? " [ADMIN]" : " [CUSTOMER]");
    }
}
