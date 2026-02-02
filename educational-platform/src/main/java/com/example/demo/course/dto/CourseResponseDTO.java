package com.example.demo.course.dto;

import com.example.demo.student.dto.StudentSummaryResponse;
import com.example.demo.teacher.dto.TeacherSummaryResponse;

import java.util.List;

public record CourseResponseDTO(Long id, String name, String description, boolean status, TeacherSummaryResponse teacherCourse,
                                List<StudentSummaryResponse> listStudents) {
}