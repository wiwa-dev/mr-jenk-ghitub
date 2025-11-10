package com.buy01.user.dto;

import com.buy01.user.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // Lombok annotation to generate getters, setters, toString, equals and hashCode
public class RegisterRequest {
    @NotBlank(message = "firstName is required")
    @Size(min = 2, max = 50, message = "firstName must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Size(min = 2, max = 50, message = "lastName must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password; // User password

    @NotNull(message = "Role is required")
    private Role role;

    private String avatar;
}
