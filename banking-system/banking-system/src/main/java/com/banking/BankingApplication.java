package com.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Online Banking System.
 *
 * @SpringBootApplication combines:
 *   - @Configuration      → marks this class as a source of Spring Beans
 *   - @EnableAutoConfiguration → auto-configures Spring based on classpath
 *   - @ComponentScan      → scans this package and sub-packages for components
 */
@SpringBootApplication
public class BankingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}
