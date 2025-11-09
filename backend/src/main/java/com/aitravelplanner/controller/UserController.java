package com.aitravelplanner.controller;

import com.aitravelplanner.dto.LoginResponse;
import com.aitravelplanner.dto.UserLoginRequest;
import com.aitravelplanner.dto.UserRegisterRequest;
import com.aitravelplanner.model.User;
import com.aitravelplanner.service.UserService;
import com.aitravelplanner.service.impl.SupabaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {
        // 使用Supabase进行用户注册
        Map<String, Object> supabaseResponse = supabaseAuthService.signUp(request.getEmail(), request.getPassword());
        
        // 在本地数据库中保存用户信息
        User user = userService.registerUser(request);
        
        return ResponseEntity.ok(Map.of(
            "user", user,
            "supabase", supabaseResponse
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody UserLoginRequest request) {
        // 使用Supabase进行用户登录
        Map<String, Object> supabaseResponse = supabaseAuthService.signIn(
            request.getUsername(), request.getPassword());
        
        // 从Supabase响应中获取token
        String accessToken = (String) supabaseResponse.get("access_token");
        
        // 创建登录响应
        LoginResponse response = new LoginResponse();
        response.setToken(accessToken);
        response.setUser(userService.getUserByUsername(request.getUsername()));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authorization) {
        // 从Authorization header中提取token
        String token = authorization.replace("Bearer ", "");
        
        // 使用Supabase获取用户详情
        Map<String, Object> userDetails = supabaseAuthService.getUserDetails(token);
        
        // 获取本地用户信息
        User user = userService.getUserByEmail((String) userDetails.get("email"));
        
        return ResponseEntity.ok(Map.of(
            "user", user,
            "details", userDetails
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteUser(@RequestParam String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}