package com.codewithmosh.arypto.dtos;

import com.codewithmosh.arypto.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data //solves the getter and setter issue
public class RegisterUserRequest {
    @NotBlank(message = "Firstname is required")
    @Size(max = 255, message = "Firstname must be less than 255 characters")
    private String firstName;

    @NotBlank(message = "Lastname is required")
    @Size(max = 255, message = "Lastname must be less than 255 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Lowercase(message = "Email must be in lowercase")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String password;

}
