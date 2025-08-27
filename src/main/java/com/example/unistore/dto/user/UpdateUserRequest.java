package com.example.unistore.dto.user;

public class UpdateUserRequest {

    private String username;

    private String email;


    /*getter and setter*/

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
