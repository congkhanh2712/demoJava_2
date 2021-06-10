package com.example.demo.message.response;

import javax.validation.constraints.NotBlank;

public class ResponseForm {
    @NotBlank
    public Boolean succeed;

    public String message;

    public ResponseForm(){
        this.message = "";
        this.succeed = false;
    }

    public ResponseForm(Boolean succeed, String message){
        this.message = message;
        this.succeed = succeed;
    }
}
