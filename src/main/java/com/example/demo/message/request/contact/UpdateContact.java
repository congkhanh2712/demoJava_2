package com.example.demo.message.request.contact;

import javax.validation.constraints.Email;

public class UpdateContact {
    private String name;

    @Email
    private String email;

    private String phone;

    private String address;
    
    public String getEmail(){
        return email;
    }
    
    public String getName(){
        return name;
    }

    public String getPhone(){
        return phone;
    }

    public String getAddress(){
        return address;
    }
}