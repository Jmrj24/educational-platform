package com.example.demo.application.teacher;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.course.CourseTestDataFactory;
import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherRepository;
import com.example.demo.teacher.TeacherTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AssignTeacherToCourseServiceTest {
    private final CourseRepository courseRepository = Mockito.mock(CourseRepository.class);
    private final TeacherRepository teacherRepository = Mockito.mock(TeacherRepository.class);

    private final AssignTeacherToCourseService assignTeacherToCourseService = new AssignTeacherToCourseService(courseRepository, teacherRepository);

    @Test
    @DisplayName("Debe lanzar una excepcion si el curso no existe")
    void assign_courseNoExist_runException() {
        Long idCourse = 20L;
        Long idTeacher = 45L;

        when(courseRepository.findById(idCourse)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            assignTeacherToCourseService.assign(idCourse, idTeacher);
        });

        verify(teacherRepository, never()).findById(idTeacher);
    }

    @Test
    @DisplayName("Debe lanzar una excepcion si el profesor no existe")
    void assign_teacherNoExist_runException() {
        Long idCourse = 20L;
        Long idTeacher = 45L;
        Course course = new Course();

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(teacherRepository.findById(idTeacher)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            assignTeacherToCourseService.assign(idCourse, idTeacher);
        });
    }

    @Test
    @DisplayName("Debe inscribir al profesor en el curso")
    void teacherInscription_courseAndTeacherExist_inscriptionSuccess() {
        Long idCourse = 20L;
        Long idTeacher = 45L;
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        Course course = CourseTestDataFactory.createCourse();

        when(teacherRepository.findById(idTeacher)).thenReturn(Optional.of(teacher));
        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));

        assignTeacherToCourseService.assign(idCourse, idTeacher);

        assertTrue(teacher.getListCourses().contains(course));
        assertEquals(teacher, course.getTeacherCourse());
        assertTrue(course.isStatus());
    }
}
