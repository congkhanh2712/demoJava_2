package com.example.demo.message.request.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePassword {
    @NotBlank(message = "Password required")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "New Password required")
    @Size(min = 6, max = 100)
    private String newpass;

    public String getPassword() {
        return password;
    }

    public String getNewpass(){
        return newpass;
    }
}