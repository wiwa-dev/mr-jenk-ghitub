package com.buy01.user.security;

import com.buy01.user.model.User;
import com.buy01.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component("userSecurity") // Important : ce nom doit correspondre à celui utilisé dans @PreAuthorize
@RequiredArgsConstructor
public class UserSecurity {

    private final UserService userService;

    public boolean isOwnerOrAdmin(String id) {
        String currentEmail = SecurityUtils.getCurrentUserEmail();
        boolean isAdmin = SecurityUtils.isAdmin();

        User user = userService.findUserById(id).orElse(null);
        if (user == null && !isAdmin)
            return false;

        // Autorisé si ADMIN ou l'utilisateur lui-même
        return isAdmin || (user != null && user.getEmail().equals(currentEmail));
    }
}
