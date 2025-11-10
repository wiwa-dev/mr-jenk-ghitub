package com.buy01.user.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data // Lombok annotation to generate getters, setters, toString, equals and hashCode
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 1, max = 50, message = "Password cannot exceed 50 characters")
    private String password;
}