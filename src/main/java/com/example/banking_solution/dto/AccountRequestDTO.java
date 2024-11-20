package com.example.banking_solution.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountRequestDTO (@NotBlank
                                 @Email(regexp = ".+@.+\\..+",
                                         message = "Email is not correct")
                                 String email,
                                 @NotBlank
                                 String password,
                                 @NotBlank
                                 String confirmPassword) {
}
