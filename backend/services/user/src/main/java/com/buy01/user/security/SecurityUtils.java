package com.buy01.user.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.buy01.user.model.Role;

public class SecurityUtils {
    
    /**
     * Récupère l'utilisateur connecté actuellement
     * @return CustomUserDetails de l'utilisateur connecté ou null si non connecté
     */
    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }
    
    /**
     * Récupère l'email de l'utilisateur connecté
     * @return email de l'utilisateur connecté ou null si non connecté
     */
    public static String getCurrentUserEmail() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getEmail() : null;
    }
    
    /**
     * Récupère l'ID de l'utilisateur connecté
     * @return ID de l'utilisateur connecté ou null si non connecté
     */
    public static String getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getId() : null;
    }
    
    /**
     * Récupère le nom de l'utilisateur connecté
     * @return nom de l'utilisateur connecté ou null si non connecté
     */
    public static String getCurrentUserName() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getFirstName() : null;
    }
    
    /**
     * Récupère le rôle de l'utilisateur connecté
     * @return rôle de l'utilisateur connecté ou null si non connecté
     */
    public static Role getCurrentUserRole() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getRole() : null;
    }
    
    /**
     * Vérifie si l'utilisateur connecté a le rôle SELLER
     * @return true si l'utilisateur est SELLER, false sinon
     */
    public static boolean isSeller() {
        Role role = getCurrentUserRole();
        return "SELLER".equals(role);
    }
     /**
     * Vérifie si l'utilisateur connecté a le rôle ADMIN
     * @return true si l'utilisateur est ADMIN, false sinon
     */
    public static boolean isAdmin() {
        Role role = getCurrentUserRole();
        return "ADMIN".equals(role);
    }
    
    /**
     * Vérifie si l'utilisateur connecté a le rôle CLIENT
     * @return true si l'utilisateur est CLIENT, false sinon
     */
    public static boolean isClient() {
        Role role = getCurrentUserRole();
        return "CLIENT".equals(role);
    }
    
    /**
     * Vérifie si l'utilisateur est connecté
     * @return true si l'utilisateur est connecté, false sinon
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
} 