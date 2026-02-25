package com.example.demo.application.teacher;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
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
        Course course = this.createCourse();

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
        Teacher teacher = this.createTeacher();
        Course course = this.createCourse();

        when(teacherRepository.findById(idTeacher)).thenReturn(Optional.of(teacher));
        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));

        assignTeacherToCourseService.assign(idCourse, idTeacher);

        assertTrue(teacher.getListCourses().contains(course));
        assertEquals(teacher, course.getTeacherCourse());
        assertTrue(course.isStatus());
    }

    private Course createCourse() {
        return new Course(20L, "Logica de programacion", "aspectos basicos", false, null, new ArrayList<>());
    }

    private Teacher createTeacher() {
        return new Teacher(45L, "Pedro", "pedro@mail.com",  "Matematicas", new ArrayList<>());
    }
}
