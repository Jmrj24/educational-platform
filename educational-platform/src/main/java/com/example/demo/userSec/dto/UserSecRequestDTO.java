package com.example.demo.userSec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UserSecRequestDTO(@NotBlank String username, @NotBlank String password, @NotNull boolean enabled,
                                @NotNull boolean accountNotExpired, @NotNull boolean accountNotLocked,
                                @NotNull boolean credentialNotExpired, @NotEmpty Set<Long> rolesListIds) {

}