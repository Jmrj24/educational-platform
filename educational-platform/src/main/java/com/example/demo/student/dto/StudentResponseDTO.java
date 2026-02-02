package com.example.demo.student.dto;

import com.example.demo.course.dto.CourseSummaryResponse;

import java.util.List;

public record StudentResponseDTO(Long id, String name, String mail, List<CourseSummaryResponse> listCourses) {
}