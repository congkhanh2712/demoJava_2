package com.example.demo.message.response.auth;

import com.example.demo.message.response.ResponseForm;

public class LoginResponse extends ResponseForm {
    private String token;
    private String type = "Bearer";

    public LoginResponse(Boolean succeed, String message, String accessToken) {
        this.token = accessToken;
        this.succeed = succeed;
        this.message = message;
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

}