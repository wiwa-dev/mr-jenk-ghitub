package com.buy01.user.security;

import com.buy01.user.model.User;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.buy01.user.model.Role;
import java.util.Collection;
import java.util.Collections;


@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    
    private final User user;
    
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getEmail(); // Utilise l'email comme username pour l'authentification
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true; // Par défaut, le compte n'expire pas
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true; // Par défaut, le compte n'est pas verrouillé
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Par défaut, les credentials n'expirent pas
    }
    
    @Override
    public boolean isEnabled() {
        return true; // Par défaut, le compte est activé
    }
    
    // Méthodes supplémentaires pour accéder aux données utilisateur
    public String getId() {
        return user.getId();
    }
    
    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public Role getRole() {
        return user.getRole();
    }
    
    public User getUser() {
        return user;
    }
    public String getAvatar(){
        return user.getAvatar();
    }
} 