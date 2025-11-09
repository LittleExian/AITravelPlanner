package com.aitravelplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
@ComponentScan("com.aitravelplanner")
public class Application {

    public static void main(String[] args) {
        System.out.println("AITravelPlanner Backend Application Starting...");
        SpringApplication.run(Application.class, args);
    }
}