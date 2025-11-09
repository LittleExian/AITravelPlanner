package com.aitravelplanner.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String token;
    private String type = "Bearer";
    private UserResponse user;
}