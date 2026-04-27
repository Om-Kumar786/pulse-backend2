package com.pulse.backend.user;

import com.pulse.backend.common.ApiException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        validateUnique(null, request.username(), request.email(), request.mobile());

        User user = new User();
        user.setUsername(request.username().trim());
        user.setEmail(normalizeEmail(request.email()));
        user.setMobile(normalizeMobile(request.mobile()));
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(parseRole(request.role()));
        user.setActive(request.active() == null ? Boolean.TRUE : request.active());

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUser(id);

        String nextUsername = request.username() == null ? user.getUsername() : request.username().trim();
        String nextEmail = request.email() == null ? user.getEmail() : normalizeEmail(request.email());
        String nextMobile = request.mobile() == null ? user.getMobile() : normalizeMobile(request.mobile());

        validateUnique(id, nextUsername, nextEmail, nextMobile);

        user.setUsername(nextUsername);
        user.setEmail(nextEmail);
        user.setMobile(nextMobile);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.role() != null && !request.role().isBlank()) {
            user.setRole(parseRole(request.role()));
        }
        if (request.active() != null) {
            user.setActive(request.active());
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public String deleteUser(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
        return "User deleted successfully";
    }

    @Transactional
    public UserResponse updateAccess(Long id, boolean active) {
        User user = findUser(id);
        user.setActive(active);
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public User findByIdentity(String username, String email, String mobile) {
        if (username != null && !username.isBlank()) {
            return userRepository.findByUsernameIgnoreCase(username.trim()).orElse(null);
        }
        if (email != null && !email.isBlank()) {
            return userRepository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT)).orElse(null);
        }
        if (mobile != null && !mobile.isBlank()) {
            return userRepository.findByMobile(normalizeMobile(mobile)).orElse(null);
        }
        return null;
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void validateUnique(Long currentUserId, String username, String email, String mobile) {
        userRepository.findByUsernameIgnoreCase(username)
                .ifPresent(existing -> {
                    if (!Objects.equals(existing.getId(), currentUserId)) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Username already exists");
                    }
                });

        if (email != null && !email.isBlank()) {
            userRepository.findByEmailIgnoreCase(email)
                    .ifPresent(existing -> {
                        if (!Objects.equals(existing.getId(), currentUserId)) {
                            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already exists");
                        }
                    });
        }

        if (mobile != null && !mobile.isBlank()) {
            userRepository.findByMobile(mobile)
                    .ifPresent(existing -> {
                        if (!Objects.equals(existing.getId(), currentUserId)) {
                            throw new ApiException(HttpStatus.BAD_REQUEST, "Mobile already exists");
                        }
                    });
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeMobile(String mobile) {
        if (mobile == null || mobile.isBlank()) {
            return null;
        }
        return mobile.replaceAll("\\D", "");
    }

    private Role parseRole(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            return Role.USER;
        }
        try {
            return Role.valueOf(rawRole.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Role must be USER or ADMIN");
        }
    }
}
