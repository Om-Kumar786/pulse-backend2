package com.pulse.backend.user;

public record UserResponse(
        Long id,
        String username,
        String email,
        String mobile,
        String role,
        Boolean active
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getMobile(),
                user.getRole().name().toLowerCase(),
                user.getActive()
        );
    }
}
