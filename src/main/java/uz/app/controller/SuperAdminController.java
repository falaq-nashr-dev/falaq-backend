package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.AdminOperatorDTO;
import uz.app.dto.UserDTO;
import uz.app.entity.User;
import uz.app.entity.enums.Role;
import uz.app.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*  PHONE-NUMBER: 111,  PASSWORD: super111.   For logining to system     */
@RestController
@RequestMapping("api/super-admin")
@RequiredArgsConstructor
@Tag(name = "Super Admin Controller",description = "Only Super Admin can manage")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<UserDTO> admins = userRepository
                .findAll()
                .stream()
                .filter(user -> user.getRole() == Role.USER)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBirthYear(),
                        user.getPhoneNumber(),
                        user.getRole()
                ))
                .toList();

        return ResponseEntity.ok(admins);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserDTO>> getAdmins() {
        List<UserDTO> admins = userRepository
                .findAll()
                .stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBirthYear(),
                        user.getPhoneNumber(),
                        user.getRole()
                ))
                .toList();

        return ResponseEntity.ok(admins);
    }

    @GetMapping("/operators")
    public ResponseEntity<List<UserDTO>> getOperators() {
        List<UserDTO> operators = userRepository
                .findAll()
                .stream()
                .filter(user -> user.getRole() == Role.OPERATOR)
                .map(user -> new UserDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBirthYear(),
                        user.getPhoneNumber(),
                        user.getRole()
                ))
                .toList();

        return ResponseEntity.ok(operators);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Admin not found");
        }

        User user = userOptional.get();

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().body("User is not an admin");
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

    @GetMapping("/operator/{id}")
    public ResponseEntity<?> getOperatorById(@PathVariable UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Operator not found");
        }

        User user = userOptional.get();

        if (user.getRole() != Role.OPERATOR) {
            return ResponseEntity.badRequest().body("User is not an operator");
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        if (user.getRole() == Role.USER || user.getRole() == Role.ADMIN || user.getRole() == Role.OPERATOR) {
            return ResponseEntity.badRequest().body("I cannot show it, because it is not a super admin");
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

    @PostMapping("/add-admin")
    public ResponseEntity<?> addAdmin(@RequestBody AdminOperatorDTO dto) {
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Phone number is already in use");
        }

        User admin = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(dto.getPhoneNumber())
                .birthYear(dto.getBirthYear())
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        userRepository.save(admin);

        return ResponseEntity.ok("Admin added successfully.");
    }

    @PostMapping("/add-operator")
    public ResponseEntity<?> addOperator(@RequestBody AdminOperatorDTO dto) {
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Phone number is already in use");
        }

        User operator = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(dto.getPhoneNumber())
                .birthYear(dto.getBirthYear())
                .role(Role.OPERATOR)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        userRepository.save(operator);

        return ResponseEntity.ok("Operator added successfully.");
    }

    @PutMapping("/edit-user/{id}")
    public ResponseEntity<?> editUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User existingUser = optionalUser.get();

        if (existingUser.getRole() == Role.SUPER_ADMIN) {
            return ResponseEntity.status(403).body("Cannot edit a super admin");
        }

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setBirthYear(userDTO.getBirthYear());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setRole(existingUser.getRole());

        User updatedUser = userRepository.save(existingUser);

        UserDTO updatedUserDTO = new UserDTO(
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getBirthYear(),
                updatedUser.getPhoneNumber(),
                updatedUser.getRole()
        );

        return ResponseEntity.ok(updatedUserDTO);
    }

    @DeleteMapping("/delete-admin/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Admin not found");
        }

        User user = userOptional.get();

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().body("User is not an admin");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok().body("Admin deleted successfully");
    }

    @DeleteMapping("/delete-operator/{id}")
    public ResponseEntity<?> deleteOperator(@PathVariable UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Operator not found");
        }

        User user = userOptional.get();

        if (user.getRole() != Role.OPERATOR) {
            return ResponseEntity.badRequest().body("User is not an operator");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok().body("Operator deleted successfully");
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (user.get().getRole() == Role.SUPER_ADMIN) {
            return ResponseEntity.status(403).body("Cannot delete a super admin");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok().body("User deleted successfully");
    }
}