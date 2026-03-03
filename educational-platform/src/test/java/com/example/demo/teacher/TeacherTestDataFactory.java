package com.example.demo.teacher;

import com.example.demo.teacher.dto.TeacherRequestDTO;

import java.util.ArrayList;
import java.util.HashSet;

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
}
