package com.banking.config;

import com.banking.dto.BankingDTOs.RegisterRequest;
import com.banking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;

    @Override
    public void run(String... args) throws Exception {
        try {
            authService.register(new RegisterRequest("admin", "admin@example.com", "password123"));
            System.out.println("=======================================================");
            System.out.println("Default User Created:");
            System.out.println("Username: admin");
            System.out.println("Password: password123");
            System.out.println("=======================================================");
        } catch (Exception e) {
            // User likely already exists
        }
    }
}
