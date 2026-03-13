package com.example.demo.application.teacher;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.course.CourseTestDataFactory;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherRepository;
import com.example.demo.teacher.TeacherTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignTeacherToCourseServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @InjectMocks
    private AssignTeacherToCourseService assignTeacherToCourseService;

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
    @DisplayName("Debe lanzar una excepcion si el curso ya tiene profesor")
    void assign_courseIncludeTeacher_runException() {
        Long idCourse = 20L;
        Long idTeacher = 45L;
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        Course course = CourseTestDataFactory.createCourse();
        course.setTeacherCourse(teacher);
        teacher.getListCourses().add(course);

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(teacherRepository.findById(idTeacher)).thenReturn(Optional.of(teacher));

        assertThrows(ConflictException.class, () -> {
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
