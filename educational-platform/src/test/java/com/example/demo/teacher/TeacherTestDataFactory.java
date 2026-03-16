package com.example.demo.teacher;

import com.example.demo.course.dto.CourseSummaryResponse;
import com.example.demo.teacher.dto.TeacherRequestDTO;
import com.example.demo.teacher.dto.TeacherResponseDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TeacherTestDataFactory {
    public static TeacherRequestDTO createValidRequest() {
        return new TeacherRequestDTO(
                "Mateo28",
                "1234",
                true,
                true,
                true,
                true,
                new HashSet<>(),
                "Mateo",
                "mateo@mail.com",
                "Matematicas");
    }

    public static Teacher createTeacher() {
        return new Teacher(
                45L,
                "Pedro",
                "pedro@mail.com",
                "Matematicas",
                new ArrayList<>());
    }

    public static Teacher createTeacherFromRequest(TeacherRequestDTO request, Long idTeacher) {
        return new Teacher(
                idTeacher,
                request.name(),
                request.mail(),
                request.specialty(),
                new ArrayList<>());
    }

    public static TeacherResponseDTO createTeacherResponseDTO(Teacher teacher, List<CourseSummaryResponse> listCourses) {
        return new TeacherResponseDTO(
                teacher.getId(),
                teacher.getName(),
                teacher.getMail(),
                teacher.getSpecialty(),
                listCourses
        );
    }

    public static TeacherResponseDTO createTeacherResponseDefault() {
        return new TeacherResponseDTO(
                37L,
                "Mateo",
                "mateo@mail.com",
                "Ciencias",
                new ArrayList<>()
        );
    }

    public static Teacher createTeacherFromIntegration() {
        Teacher teacher = new Teacher();
        teacher.setName("Mateo");
        teacher.setMail("mateo@mail.com");
        teacher.setSpecialty("Matematicas");
        return teacher;
    }
}