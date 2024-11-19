package com.example.banking_solution.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class ApiConfig {

    @Bean
    Random getRandom() {
        return new Random();
    }
}
