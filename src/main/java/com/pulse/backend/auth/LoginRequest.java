package com.pulse.backend.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        String username,
        String email,
        String mobile,

        @NotBlank(message = "Password is required")
        String password
) {
}
