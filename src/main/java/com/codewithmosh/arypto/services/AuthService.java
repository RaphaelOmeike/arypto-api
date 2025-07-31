package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.entities.User;
import com.codewithmosh.arypto.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (String) authentication.getPrincipal();

        return userRepository.findById(userId).orElse(null);
    }
}
