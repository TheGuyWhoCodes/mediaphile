package com.google.sps.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity(name="User")
public class UserObject {

    @Id
    @Index
    @JsonProperty
    private String id;

    @JsonProperty
    @Index
    private String username;

    @JsonProperty
    @Index
    private String email;

    @JsonProperty
    @Index
    private String profilePicUrl;

    public UserObject() {} // For Objectify

    public UserObject(String id, String username, String email, String profilePicUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePicUrl = profilePicUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}

