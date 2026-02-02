package com.example.demo.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record StudentRequestDTO(@NotBlank String username, @NotBlank String password, @NotNull boolean enabled,
                                @NotNull boolean accountNotExpired, @NotNull boolean accountNotLocked,
                                @NotNull boolean credentialNotExpired, @NotEmpty Set<Long> rolesListIds,
                                @NotBlank String name, @Email String mail) {
}