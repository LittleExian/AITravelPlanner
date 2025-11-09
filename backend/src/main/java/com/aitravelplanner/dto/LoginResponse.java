package com.aitravelplanner.dto;

import com.aitravelplanner.model.User;
import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private User user;
}