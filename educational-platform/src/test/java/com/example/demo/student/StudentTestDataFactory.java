package com.example.demo.student;

import com.example.demo.course.dto.CourseSummaryResponse;
import com.example.demo.student.dto.StudentRequestDTO;
import com.example.demo.student.dto.StudentResponseDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StudentTestDataFactory {
    public static StudentRequestDTO createValidRequest() {
        return new StudentRequestDTO(
                "Maria85",
                "1234",
                true,
                true,
                true,
                true,
                new HashSet<>(),
                "Maria",
                "maria@mail.com");
    }

    public static Student createStudent() {
        return new Student(
                89L,
                "Juan",
                "juan@mail.com",
                new ArrayList<>());
    }

    public static Student createStudentFromRequest(StudentRequestDTO request, Long idStudent) {
        return new Student(
                idStudent,
                request.name(),
                request.mail(),
                new ArrayList<>());
    }

    public static StudentResponseDTO createStudentResponseDTO(Student student, List<CourseSummaryResponse> listCourses) {
        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getMail(),
                listCourses
        );
    }

    public static StudentResponseDTO createStudentResponseDefault() {
        return new StudentResponseDTO(
                12L,
                "Maria",
                "maria@mail.com",
                new ArrayList<>()
        );
    }
}