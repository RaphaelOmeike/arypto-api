package com.codewithmosh.arypto.controllers;

import com.codewithmosh.arypto.dtos.ErrorDto;
import com.codewithmosh.arypto.dtos.JwtResponse;
import com.codewithmosh.arypto.dtos.LoginRequest;
import com.codewithmosh.arypto.dtos.UserDto;
import com.codewithmosh.arypto.services.AuthService;
import com.codewithmosh.arypto.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;

@AllArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
           @CookieValue(value = "refreshToken") String refreshToken
    ) {
        var response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        var result = authService.login(request, response);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/validate")
    public boolean validate(@RequestHeader("Authorization") String authHeader) {
        return userService.validateToken(authHeader);
    }
    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        return ResponseEntity.ok(userService.getMe());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUsernameNotFound(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(ex.getMessage()));
    }

}
