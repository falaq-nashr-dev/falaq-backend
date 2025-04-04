package uz.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.app.config.LocalDateTimeAttributeConverter;
import uz.app.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String firstName;
    private String lastName;
    private Integer birthYear;

    private String phoneNumber;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime createdAt;

    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.phoneNumber;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

}
