package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.dtos.ChangePasswordRequest;
import com.codewithmosh.arypto.dtos.RegisterUserRequest;
import com.codewithmosh.arypto.dtos.UpdateUserRequest;
import com.codewithmosh.arypto.dtos.UserDto;
import com.codewithmosh.arypto.entities.Role;
import com.codewithmosh.arypto.exceptions.EmailAlreadyExistsException;
import com.codewithmosh.arypto.exceptions.PasswordMismatchException;
import com.codewithmosh.arypto.mappers.UserMapper;
import com.codewithmosh.arypto.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(email)
        );
        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }


    public UserDto fetchUserWithWallets(String userId) {
        var user = userRepository.getUserWithWallets(userId).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException(userId); // or throw an exception if preferred
        }
        return userMapper.toDto(user);
    }


    @PostMapping
    public UserDto registerUser(
            RegisterUserRequest userRequest
    ) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + userRequest.getEmail());
        }
        var user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);
        return userMapper.toDto(user);
    }


    public UserDto updateUser(
            String userId,
            UpdateUserRequest request
    ) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(userId);
        }

        userMapper.update(request, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void deleteUser(String userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(userId);
        }

        userRepository.delete(user);
    }

    public void changePassword(
            String userId,
            ChangePasswordRequest request
    ) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(userId);
        }
        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);

    }

    public Iterable<UserDto> getAllUsers(
            String sortBy
    ) {
        if (!Set.of("name", "email").contains(sortBy)) {
            sortBy = "name";
        }
        return userRepository.findAll(Sort.by(sortBy))
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
