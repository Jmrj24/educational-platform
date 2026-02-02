package com.example.demo.permission.dto;

import jakarta.validation.constraints.NotBlank;

public record PermissionRequestDTO (@NotBlank String permissionName) {}
