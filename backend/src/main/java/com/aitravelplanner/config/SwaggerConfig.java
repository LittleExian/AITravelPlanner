package com.aitravelplanner.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI旅行规划助手 API")
                        .version("1.0.0")
                        .description("AI旅行规划助手后端API文档，提供用户管理、行程规划、预算管理等功能")
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")));
    }
}