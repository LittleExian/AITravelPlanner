package com.aitravelplanner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupabaseAuthConfig {

    @Value("${supabase.jwt.secret}")
    private String supabaseJwtSecret;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service.role.key}")
    private String supabaseServiceRoleKey;

    @Bean
    public String supabaseJwtSecret() {
        return supabaseJwtSecret;
    }

    @Bean
    public String supabaseUrl() {
        return supabaseUrl;
    }

    @Bean
    public String supabaseServiceRoleKey() {
        return supabaseServiceRoleKey;
    }
}