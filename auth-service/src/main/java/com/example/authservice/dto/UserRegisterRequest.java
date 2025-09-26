package com.example.authservice.dto;

import com.example.authservice.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(@Email @NotBlank String email,
                                  @NotBlank @Size(min = 6) String password,
                                  @NotNull Role role) {
}
