package com.pulse.backend.auth;

public record AuthResponse(
        Long id,
        String username,
        String email,
        String mobile,
        String role,
        Boolean active,
        String message
) {
}
