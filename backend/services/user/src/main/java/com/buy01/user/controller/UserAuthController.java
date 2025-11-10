package com.buy01.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.buy01.user.util.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;

import com.buy01.user.model.*;
import com.buy01.user.security.CustomUserDetails;
import com.buy01.user.dto.*;
import com.buy01.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authentifier l'utilisateur
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            // Charger les détails de l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

            // Générer le token JWT
            String token = jwtUtil.generateToken(userDetails);

            // Récupérer les informations utilisateur depuis CustomUserDetails
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            // Créer l'objet UserResponse
            UserResponse userResponse = new UserResponse(
                    customUserDetails.getFirstName(),
                    customUserDetails.getLastName(),
                    customUserDetails.getEmail(),
                    customUserDetails.getRole(),
                    customUserDetails.getAvatar());
            // Créer la réponse
            LoginResponse response = new LoginResponse(
                    token,
                    userResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                    401,
                    "Unauthorised",
                    "Invalid email or password");
            return ResponseEntity.status(401).body(error);
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest user) {
        // ✅ Vérifier si l'utilisateur existe déjà
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409
                    .body("User already exists");
        }

        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setRole(user.getRole());
        newUser.setAvatar(user.getAvatar());
        User createdUser = userService.createUser(newUser);

        UserResponse userDTO = new UserResponse();
        userDTO.setFirstName(createdUser.getFirstName());
        userDTO.setLastName(createdUser.getLastName());
        userDTO.setEmail(createdUser.getEmail());
        userDTO.setRole(createdUser.getRole());
        return ResponseEntity.ok(userDTO);
    }
}
