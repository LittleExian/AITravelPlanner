package com.aitravelplanner.service.impl;

import com.aitravelplanner.config.SupabaseAuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseAuthService {

    @Autowired
    private SupabaseAuthConfig supabaseAuthConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> signUp(String email, String password) {
        String url = supabaseAuthConfig.supabaseUrl() + "/auth/v1/signup";
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        
        return restTemplate.postForObject(url, requestBody, Map.class);
    }

    public Map<String, Object> signIn(String email, String password) {
        String url = supabaseAuthConfig.supabaseUrl() + "/auth/v1/token?grant_type=password";
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        
        return restTemplate.postForObject(url, requestBody, Map.class);
    }

    public Map<String, Object> getUserDetails(String accessToken) {
        String url = supabaseAuthConfig.supabaseUrl() + "/auth/v1/user";
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        
        // 使用带有头部的请求
        return restTemplate.getForObject(url, Map.class);
    }
}