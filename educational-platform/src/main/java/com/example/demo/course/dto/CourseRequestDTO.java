package com.example.demo.course.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseRequestDTO(@NotBlank String name, @NotBlank String description) {
}
