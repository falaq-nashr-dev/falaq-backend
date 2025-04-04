package uz.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.app.entity.User;
import uz.app.entity.enums.Role;
import uz.app.repository.UserRepository;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataGenerator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initializeSuperAdmin() {
        return args -> {
            userRepository.findByPhoneNumber("111").ifPresentOrElse(
                    existingAdmin -> {
                        existingAdmin.setFirstName("Super");
                        existingAdmin.setLastName("Admin");
                        existingAdmin.setBirthYear(2000);
                        existingAdmin.setPassword(passwordEncoder.encode("super111"));
                        existingAdmin.setRole(Role.SUPER_ADMIN);
                        existingAdmin.setCreatedAt(LocalDateTime.now());
                        existingAdmin.setEnabled(true);

                        userRepository.save(existingAdmin);
                        System.out.println("Super Admin updated successfully!");
                    },
                    () -> {
                        User superAdmin = User.builder()
                                .firstName("Super")
                                .lastName("Admin")
                                .phoneNumber("111")
                                .birthYear(2000)
                                .password(passwordEncoder.encode("super111"))
                                .role(Role.SUPER_ADMIN)
                                .createdAt(LocalDateTime.now())
                                .enabled(true)
                                .build();

                        userRepository.save(superAdmin);
                        System.out.println("Default Super Admin created!");
                    }
            );
        };
    }
}

