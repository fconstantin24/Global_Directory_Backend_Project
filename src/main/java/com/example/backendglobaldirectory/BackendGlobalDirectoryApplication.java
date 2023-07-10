package com.example.backendglobaldirectory;

import com.example.backendglobaldirectory.entities.Roles;
import com.example.backendglobaldirectory.entities.User;
import com.example.backendglobaldirectory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class BackendGlobalDirectoryApplication {

    @Bean
    CommandLineRunner commandLineRunner(
            @Value("${user.admin.email}") String adminEmail,
            @Value("${user.admin.password}") String adminPassword,
            UserRepository userRepository) {
        return args -> {
            if(userRepository.findByEmail(adminEmail).isPresent()) {
                return;
            }

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            User adminUser = new User();
            adminUser.setApproved(true);
            adminUser.setActive(true);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(bCryptPasswordEncoder.encode(adminPassword));
            adminUser.setRole(Roles.ADMIN);

            userRepository.save(adminUser);

        };
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendGlobalDirectoryApplication.class, args);
    }

}
