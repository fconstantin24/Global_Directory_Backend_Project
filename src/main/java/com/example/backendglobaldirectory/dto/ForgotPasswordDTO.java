package com.example.backendglobaldirectory.dto;

import lombok.Data;

@Data
public class ForgotPasswordDTO {
    private String password;
    private String confirmPassword;

    public String getPassword() {
        return password != null ? password : "";
    }
}
