package com.aitravelplanner.controller;

import com.aitravelplanner.dto.TokenResponse;
import com.aitravelplanner.dto.UserLoginRequest;
import com.aitravelplanner.dto.UserRegisterRequest;
import com.aitravelplanner.dto.UserResponse;
import com.aitravelplanner.model.User;
import com.aitravelplanner.security.JwtUtils;
import com.aitravelplanner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.registerUser(request);
        UserResponse userResponse = userService.getUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        UserResponse userResponse = userService.getUserResponse(user);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(jwt);
        tokenResponse.setUser(userResponse);

        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        User user = userService.getCurrentUser();
        UserResponse userResponse = userService.getUserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody User user) {
        User currentUser = userService.getCurrentUser();
        User updatedUser = userService.updateUser(currentUser.getId(), user);
        UserResponse userResponse = userService.getUserResponse(updatedUser);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<?> deleteUserProfile() {
        User currentUser = userService.getCurrentUser();
        userService.deleteUser(currentUser.getId());
        return ResponseEntity.ok("用户已删除");
    }
}