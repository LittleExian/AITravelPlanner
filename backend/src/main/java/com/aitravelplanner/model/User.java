package com.aitravelplanner.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password;  // 加密后的密码
    private String fullName;
    private String phone;  // 电话号码
    private String bio;  // 个人简介
    private Date createdAt;
    private Date updatedAt;
    private String avatar;
    private boolean active;
}