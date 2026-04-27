package com.pulse.backend.auth;

import com.pulse.backend.common.ApiException;
import com.pulse.backend.user.CreateUserRequest;
import com.pulse.backend.user.User;
import com.pulse.backend.user.UserResponse;
import com.pulse.backend.user.UserService;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        UserResponse created = userService.createUser(new CreateUserRequest(
                request.username(),
                request.email(),
                request.mobile(),
                request.password(),
                request.role(),
                true
        ));

        return new AuthResponse(
                created.id(),
                created.username(),
                created.email(),
                created.mobile(),
                created.role(),
                created.active(),
                "Registration successful"
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if ((request.username() == null || request.username().isBlank())
                && (request.email() == null || request.email().isBlank())
                && (request.mobile() == null || request.mobile().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Provide username, email, or mobile for login");
        }

        User user = userService.findByIdentity(request.username(), request.email(), request.mobile());

        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "This account is currently deactivated. Please contact an administrator.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getMobile(),
                user.getRole().name().toLowerCase(Locale.ROOT),
                user.getActive(),
                "Login successful"
        );
    }
}
