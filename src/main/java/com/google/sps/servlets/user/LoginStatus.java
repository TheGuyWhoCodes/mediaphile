package com.google.sps.servlets.user;

public class LoginStatus {
    private boolean logged_in;
    private String url;
    private String id;

    public LoginStatus(boolean logged_in, String url, String id) {
        this.logged_in = logged_in;
        this.url = url;
        this.id = id;
    }

    public boolean isLoggedIn() {
        return logged_in;
    }

    public void setLoggedIn(boolean logged_in) {
        this.logged_in = logged_in;
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