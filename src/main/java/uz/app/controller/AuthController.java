package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.app.config.JwtProvider;
import uz.app.dto.ChangePasswordDTO;
import uz.app.dto.LoginDTO;
import uz.app.dto.SignUpDTO;
import uz.app.dto.UserDTO;
import uz.app.entity.User;
import uz.app.entity.enums.Role;
import uz.app.repository.UserRepository;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authorization Controller",description = "This is for entering to system")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDTO signUpDTO) {
        if (userRepository.existsByPhoneNumber(signUpDTO.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Phone number is already in use");
        }

        User user = User.builder()
                .firstName(signUpDTO.getFirstName())
                .lastName(signUpDTO.getLastName())
                .password(passwordEncoder.encode(signUpDTO.getPassword()))
                .phoneNumber(signUpDTO.getPhoneNumber())
                .birthYear(signUpDTO.getBirthYear())
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        userRepository.save(user);

        String token = jwtProvider.generateToken(user);
        return ResponseEntity.ok("Here is your token: " + token);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginDTO) {
        User user = userRepository.findByPhoneNumber(loginDTO.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Wrong password");
        }

        String token = jwtProvider.generateToken(user);
        return ResponseEntity.ok("Here is your token: " + token);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Tag(name = "User's Profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.badRequest().body("User not authenticated");
        }

        UserDTO userDTO = new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getBirthYear(),
                user.getPhoneNumber(),
                user.getRole()
        );

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal User user, @RequestBody ChangePasswordDTO dto) {
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password updated successfully");
    }
}
