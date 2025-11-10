package com.buy01.user.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.buy01.user.repository.UserRepository;
import com.buy01.user.model.User;

import java.util.Optional;
import java.util.List;



@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findUserById(String id) {
        return Optional.ofNullable(userRepository.findById(id).orElse(null));
    }

    
    public Optional<List<User>> findAllUserByRole(String role){
            return userRepository.findAllByRole(role);
    }
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
