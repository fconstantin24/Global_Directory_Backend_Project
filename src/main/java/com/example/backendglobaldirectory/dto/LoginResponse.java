package com.example.backendglobaldirectory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private int userId;
    private String token;
}
