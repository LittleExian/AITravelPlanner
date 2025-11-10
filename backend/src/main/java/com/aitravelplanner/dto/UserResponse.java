package com.aitravelplanner.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String bio;
    private Date createdAt;
    private String avatar;
    private boolean active;
}