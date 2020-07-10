package com.google.sps.model.user;

public class LoginStatus {
    private boolean loggedIn;
    private String url;
    private String id;

    public LoginStatus() {}

    public LoginStatus(boolean logged_in, String url, String id) {
        this.loggedIn = logged_in;
        this.url = url;
        this.id = id;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}