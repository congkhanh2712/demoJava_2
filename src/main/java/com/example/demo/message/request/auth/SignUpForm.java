package com.example.demo.message.request.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignUpForm {
    @NotBlank(message = "Username required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password required")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Name required")
    @Size(min = 3, max = 50)
    private String name;

    private String address;

    private String phone;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}
