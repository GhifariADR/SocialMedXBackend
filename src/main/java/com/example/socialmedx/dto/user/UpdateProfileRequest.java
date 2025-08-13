package com.example.socialmedx.dto.user;

public class UpdateProfileRequest {

    private String bio;

    private String profilePicture;

    /*getter and setter*/

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
