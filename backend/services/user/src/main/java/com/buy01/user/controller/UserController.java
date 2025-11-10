package com.buy01.user.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.buy01.user.model.*;
import com.buy01.user.dto.*;
import com.buy01.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/all/{role}")
    public ResponseEntity<List<User>> getAllSellers(@PathVariable String role) {
        Optional<List<User>> sellers = userService.findAllUserByRole(role);
        return sellers.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwnerOrAdmin(#id)")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwnerOrAdmin(#id)")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> user = userService.findUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    @PreAuthorize("@userSecurity.isOwnerOrAdmin(#id)")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody @Valid RegisterRequest user) {
        // ✅ Vérifier si l'utilisateur existe déjà
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409
                    .body("email already exists");
        }
        Optional<User> existingUser = userService.findUserById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User userToUpdate = existingUser.get();

        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setPassword(user.getPassword());
        User updatedUser = userService.createUser(userToUpdate);
        return ResponseEntity.ok(updatedUser);
    }
}
