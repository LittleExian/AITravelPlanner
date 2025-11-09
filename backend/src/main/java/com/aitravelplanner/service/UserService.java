package com.aitravelplanner.service;

import com.aitravelplanner.dto.UserRegisterRequest;
import com.aitravelplanner.dto.UserResponse;
import com.aitravelplanner.model.User;

import java.util.Optional;

public interface UserService {
    User registerUser(UserRegisterRequest request);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    UserResponse getUserResponse(User user);
    User updateUser(String userId, User user);
    void deleteUser(String userId);
    User getCurrentUser();
}