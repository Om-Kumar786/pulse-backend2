package com.pulse.backend.config;

import com.pulse.backend.user.CreateUserRequest;
import com.pulse.backend.user.UserRepository;
import com.pulse.backend.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner ensureDefaultAdmin(UserRepository userRepository, UserService userService) {
        return args -> {
            if (!userRepository.existsByUsernameIgnoreCase("admin")) {
                userService.createUser(new CreateUserRequest(
                        "admin",
                        "admin@pulse.local",
                        null,
                        "admin123",
                        "ADMIN",
                        true
                ));
            }
        };
    }
}
