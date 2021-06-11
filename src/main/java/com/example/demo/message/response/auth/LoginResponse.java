package com.example.demo.message.response.auth;

import java.util.Set;

import com.example.demo.entities.Role;
import com.example.demo.message.response.ResponseForm;

public class LoginResponse extends ResponseForm {
    private String token;
    private String type = "Bearer";
    private Set<Role> roles;

    public LoginResponse(Boolean succeed, String message, String accessToken, Set<Role> roles) {
        this.token = accessToken;
        this.succeed = succeed;
        this.message = message;
        this.roles = roles;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }
    public Set<Role> getRole(){
        return roles;
    }

    public void setRole(Set<Role> role){
        this.roles = role;
    }
}