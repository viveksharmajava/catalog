package com.playpro.playpro.catalog.dto;

import java.util.List;

public class LoginResponse {

    private String username;
    private List<String> roles;
    private List<String> permissions;
    private String authHeader;

    public LoginResponse() {
    }

    public LoginResponse(String username, List<String> roles, String authHeader) {
        this.username = username;
        this.roles = roles;
        this.authHeader = authHeader;
    }

    public LoginResponse(String username, List<String> roles, List<String> permissions, String authHeader) {
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
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

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }
}
