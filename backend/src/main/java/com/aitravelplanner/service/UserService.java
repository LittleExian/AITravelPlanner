package com.aitravelplanner.service;

import com.aitravelplanner.dto.LoginResponse;
import com.aitravelplanner.dto.UserLoginRequest;
import com.aitravelplanner.dto.UserRegisterRequest;
import com.aitravelplanner.model.User;

public interface UserService {

    User registerUser(UserRegisterRequest request);
    LoginResponse loginUser(UserLoginRequest request);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    User updateUser(User user);
    void deleteUser(String userId);
    boolean isUsernameExists(String username);
    boolean isEmailExists(String email);
}