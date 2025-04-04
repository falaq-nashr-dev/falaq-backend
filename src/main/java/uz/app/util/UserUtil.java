package uz.app.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import uz.app.entity.User;
import uz.app.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByPhoneNumber(username);
        }
        return Optional.empty();
    }
}
