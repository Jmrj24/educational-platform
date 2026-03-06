package com.example.demo.teacher.mapper;

import com.example.demo.course.Course;
import com.example.demo.course.CourseTestDataFactory;
import com.example.demo.course.dto.CourseSummaryResponse;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherTestDataFactory;
import com.example.demo.teacher.dto.TeacherResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TeacherMapperTest {
    TeacherMapper teacherMapper = new TeacherMapper();
    @Test
    @DisplayName("Debe devolver un TeacherResponse si todo sale bien")
    void toTeacherResponse_teacherValid_returnTeacherResponse() {
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        Course course = CourseTestDataFactory.createCourse();
        List<Course> listCourses = new ArrayList<>(List.of(course));
        teacher.setListCourses(listCourses);
        CourseSummaryResponse courseSummary = new CourseSummaryResponse(course.getId(), course.getName(), course.getDescription(), course.isStatus());
        TeacherResponseDTO teacherResponseExpect = TeacherTestDataFactory.createTeacherResponseDTO(teacher, List.of(courseSummary));

        TeacherResponseDTO teacherResponseResult = teacherMapper.toTeacherResponse(teacher);

        assertEquals(teacherResponseExpect, teacherResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un null si el teacher de entrada es null")
    void toTeacherResponse_teacherNull_returnNull() {
        TeacherResponseDTO teacherResponseResult = teacherMapper.toTeacherResponse(null);

        assertNull(teacherResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un TeacherResponse con la lista de cursos vacias si la misma entra vacia")
    void toTeacherResponse_listCoursesEmpty_returnCourseResponse() {
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        TeacherResponseDTO teacherResponseExpect = TeacherTestDataFactory.createTeacherResponseDTO(teacher, Collections.emptyList());

        TeacherResponseDTO teacherResponseResult = teacherMapper.toTeacherResponse(teacher);

        assertEquals(teacherResponseExpect, teacherResponseResult);
    }
}