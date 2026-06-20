package com.playpro.playpro.catalog.dto;

import java.util.List;

public class LoginResponse {

    private String username;
    private List<String> roles;
    private String authHeader;

    public LoginResponse() {
    }

    public LoginResponse(String username, List<String> roles, String authHeader) {
        this.username = username;
        this.roles = roles;
        this.authHeader = authHeader;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }
}
