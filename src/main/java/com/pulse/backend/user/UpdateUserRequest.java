package com.pulse.backend.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Email(message = "Email must be valid")
        String email,

        @Pattern(regexp = "^$|^[0-9]{10}$", message = "Mobile must be 10 digits")
        String mobile,

        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password,

        String role,
        Boolean active
) {
}
