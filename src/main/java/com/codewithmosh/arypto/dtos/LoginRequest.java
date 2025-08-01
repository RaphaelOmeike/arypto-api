package com.codewithmosh.arypto.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Enter a valid email")
    @Email
    private String email;
    @NotBlank
    @Size(min = 6, message = "Password must be a minimum of 6 characters.")
    private String password;
}
