package com.healthcare.authservice;


import com.healthcare.authservice.entity.UserAccount;
import com.healthcare.authservice.repository.UserAccountRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("--- Auth Service Application ---");
    }

    @Bean
    ApplicationRunner applicationRunner(UserAccountRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (repository.findByUsername("user").isEmpty()) {

                String encodedPassword = passwordEncoder.encode("password");
                UserAccount testUser = new UserAccount(null,"user", encodedPassword, "ROLE_USER",true,true,true,true);
                repository.save(testUser);
                System.out.println("Created default user 'user' with password 'password'");
            }

            if (repository.findByUsername("admin").isEmpty()) {
                String encodedPassword = passwordEncoder.encode("adminpass");
                UserAccount adminUser = new UserAccount(null,"admin", encodedPassword, "ROLE_ADMIN",true,true,true,true);
                repository.save(adminUser);
                System.out.println("Created default user 'admin' with password 'adminpass'");
            }
        };
    }
}
