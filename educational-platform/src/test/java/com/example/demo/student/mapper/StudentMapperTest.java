package com.example.demo.student.mapper;

import com.example.demo.course.Course;
import com.example.demo.course.CourseTestDataFactory;
import com.example.demo.course.dto.CourseSummaryResponse;
import com.example.demo.student.Student;
import com.example.demo.student.StudentTestDataFactory;
import com.example.demo.student.dto.StudentResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StudentMapperTest {
    private final StudentMapper studentMapper = new StudentMapper();
    @Test
    @DisplayName("Debe devolver un StudentResponse si todo sale bien")
    void toStudentResponse_studentValid_returnStudentResponse() {
        Student student = StudentTestDataFactory.createStudent();
        Course course = CourseTestDataFactory.createCourse();
        List<Course> listCourses = new ArrayList<>(List.of(course));
        student.setListCourses(listCourses);
        CourseSummaryResponse courseSummary = new CourseSummaryResponse(course.getId(), course.getName(), course.getDescription(), course.isStatus());
        StudentResponseDTO studentResponseExpect = StudentTestDataFactory.createStudentResponseDTO(student, List.of(courseSummary));

        StudentResponseDTO studentResponseResult = studentMapper.toStudentResponse(student);

        assertEquals(studentResponseExpect, studentResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un null si el student de entrada es null")
    void toStudentResponse_studentNull_returnNull() {
        StudentResponseDTO studentResponseResult = studentMapper.toStudentResponse(null);

        assertNull(studentResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un StudentResponse con la lista de cursos vacias si la misma entra vacia")
    void toStudentResponse_listCoursesEmpty_returnStudentResponse() {
        Student student = StudentTestDataFactory.createStudent();
        StudentResponseDTO studentResponseExpect = StudentTestDataFactory.createStudentResponseDTO(student, Collections.emptyList());

        StudentResponseDTO studentResponseResult = studentMapper.toStudentResponse(student);

        assertEquals(studentResponseExpect, studentResponseResult);
    }
}