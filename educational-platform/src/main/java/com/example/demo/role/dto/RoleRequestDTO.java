package com.example.demo.role.dto;

import com.example.demo.role.NameRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RoleRequestDTO (@NotNull NameRole role, @NotEmpty Set<Long> permissionsListIds) {
}
