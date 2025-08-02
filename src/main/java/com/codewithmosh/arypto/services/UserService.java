package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.config.JwtConfig;
import com.codewithmosh.arypto.dtos.*;
import com.codewithmosh.arypto.entities.Role;
import com.codewithmosh.arypto.exceptions.EmailAlreadyExistsException;
import com.codewithmosh.arypto.exceptions.PasswordMismatchException;
import com.codewithmosh.arypto.exceptions.TokenExpiredException;
import com.codewithmosh.arypto.mappers.UserMapper;
import com.codewithmosh.arypto.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CryptoPaymentGateway paymentGateway;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

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
        var request = new CreateSubaccountRequest(
                userRequest.getEmail(),
                userRequest.getFirstName(),
                userRequest.getLastName());
        var response = paymentGateway.createSubaccount(request);

        var user = userMapper.toEntity(userRequest);
        user.setId(response.getData().getId());
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

    public JwtResponse refreshToken(String refreshToken) {
        var jwt = jwtService.parseToken(refreshToken);
        if (jwt == null || jwt.isExpired(refreshToken)) {
            throw new TokenExpiredException("Invalid or expired refresh token");
        }
        var userId = jwt.getUserId(refreshToken);
        var user = userRepository.findById(userId).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        return new JwtResponse(accessToken.toString());
    }

    public JwtResponse login(LoginRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);
        return new JwtResponse(accessToken.toString());
    }

    // ...existing code...
    public boolean validateToken(String authHeader) {
        var token = authHeader.replace("Bearer ", "");
        var jwt = jwtService.parseToken(token);
        return !jwt.isExpired(token);
    }

    public UserDto getCurrentUser(String userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(userId);
        }
        return userMapper.toDto(user);
    }

    public UserDto getMe() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (String) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException(userId);
        }

        return userMapper.toDto(user);
    }
// ...existing code...

}
