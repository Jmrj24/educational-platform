package com.example.demo.course;

import com.example.demo.course.dto.CourseResponseDTO;
import com.example.demo.student.dto.StudentSummaryResponse;
import com.example.demo.teacher.dto.TeacherSummaryResponse;

import java.util.ArrayList;
import java.util.List;

public class CourseTestDataFactory {
    public static Course createCourse() {
        return new Course(
                20L,
                "Logica de programacion",
                "aspectos basicos",
                false,
                null,
                new ArrayList<>());
    }

    public static CourseResponseDTO createCourseResponse(Course course, TeacherSummaryResponse teacher, List<StudentSummaryResponse> listStudents) {
        return new CourseResponseDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.isStatus(),
                teacher,
                listStudents);
    }
}
