package com.example.demo.teacher.dto;

import com.example.demo.course.dto.CourseSummaryResponse;

import java.util.List;

public record TeacherResponseDTO(Long id, String name, String mail, String specialty, List<CourseSummaryResponse> listCourses) {
}