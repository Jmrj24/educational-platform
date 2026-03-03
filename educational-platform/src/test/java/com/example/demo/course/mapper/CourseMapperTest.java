package com.example.demo.course.mapper;

import com.example.demo.course.Course;
import com.example.demo.course.CourseTestDataFactory;
import com.example.demo.course.dto.CourseResponseDTO;
import com.example.demo.student.Student;
import com.example.demo.student.StudentTestDataFactory;
import com.example.demo.student.dto.StudentSummaryResponse;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherTestDataFactory;
import com.example.demo.teacher.dto.TeacherSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseMapperTest {
    private final CourseMapper courseMapper = new CourseMapper();
    @Test
    @DisplayName("Debe devolver un CourseResponse si todo sale bien")
    void toCourseResponse_courseValid_returnCourseResponse() {
        Course course = CourseTestDataFactory.createCourse();
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        Student student = StudentTestDataFactory.createStudent();
        course.setStatus(true);
        course.setTeacherCourse(teacher);
        course.getListStudents().add(student);
        TeacherSummaryResponse teacherSummary = new TeacherSummaryResponse(teacher.getId(), teacher.getName(), teacher.getMail(), teacher.getSpecialty());
        StudentSummaryResponse studentSummary = new StudentSummaryResponse(student.getId(), student.getName(), student.getMail());
        CourseResponseDTO courseResponseExpect = CourseTestDataFactory.createCourseResponse(course, teacherSummary, List.of(studentSummary));

        CourseResponseDTO courseResponseResult = courseMapper.toCourseResponse(course);

        assertEquals(courseResponseExpect, courseResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un null si el curso de entrada es null")
    void toCourseResponse_courseNull_returnNull() {
        CourseResponseDTO courseResponseResult = courseMapper.toCourseResponse(null);

        assertNull(courseResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un CourseResponse con el profesor null si el profesor de entrada es null")
    void toCourseResponse_teacherNull_returnCourseResponse() {
        Course course = CourseTestDataFactory.createCourse();
        CourseResponseDTO courseResponseExpect = CourseTestDataFactory.createCourseResponse(course, null, Collections.emptyList());

        CourseResponseDTO courseResponseResult = courseMapper.toCourseResponse(course);

        assertEquals(courseResponseExpect, courseResponseResult);
    }
}