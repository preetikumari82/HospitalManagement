package com.hospital.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hospital.enums.Role;
import com.hospital.model.Admin;
import com.hospital.model.User;
import com.hospital.repository.AdminRepository;
import com.hospital.repository.UserRepository;

@Configuration
public class AdminConfig {


    @Value("${admin.name}")
    private String adminName;


    @Value("${admin.email}")
    private String adminEmail;


    @Value("${admin.password}")
    private String adminPassword;



    @Bean
    CommandLineRunner saveAdmin(
            AdminRepository adminRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {


        return args -> {


            if (!userRepository.existsByEmail(adminEmail)) {


                User user = new User();

                user.setName(adminName);
                user.setEmail(adminEmail);
                user.setActive(false);

                // Convert normal password into BCrypt password
                user.setPassword(
                    passwordEncoder.encode(adminPassword)
                );

                user.setRole(Role.ADMIN);


                // Save User first
                User savedUser = userRepository.save(user);



                Admin admin = new Admin();

                admin.setUser(savedUser);
                admin.setCreatedAt(LocalDateTime.now());


                adminRepository.save(admin);


                System.out.println("Admin saved successfully");


            } else {

                System.out.println("Admin already exists");

            }

        };
    }
}