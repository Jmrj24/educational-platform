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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UnassignTeacherToCourseServiceTest {
    private final CourseRepository courseRepository = Mockito.mock(CourseRepository.class);
    private final TeacherRepository teacherRepository = Mockito.mock(TeacherRepository.class);

    UnassignTeacherToCourseService unassignTeacherToCourseService = new UnassignTeacherToCourseService(courseRepository, teacherRepository);

    @Test
    @DisplayName("Elimina la asignacion del profesor en el curso")
    void teacherUnassign_teacherAndCourseExist_unassignExit() {
        Long idCourse = 20L;
        Long idTeacher = 45L;
        Teacher teacher = this.createTeacher();
        Course course = this.createCourse();
        teacher.getListCourses().add(course);
        course.setTeacherCourse(teacher);

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(teacherRepository.findById(idTeacher)).thenReturn(Optional.of(teacher));

        unassignTeacherToCourseService.unassign(idCourse, idTeacher);

        assertFalse(teacher.getListCourses().contains(course));
        assertNull(course.getTeacherCourse());
        assertFalse(course.isStatus());
    }

    @Test
    @DisplayName("Falla cuando el profesor no existe")
    void teacherUnassign_teacherNoExist_unassignFail() {
        Long idCourse = 20L;
        Long idTeacher = 45L;
        Course course = this.createCourse();

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(teacherRepository.findById(idTeacher)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            unassignTeacherToCourseService.unassign(idCourse, idTeacher);
        });
    }

    @Test
    @DisplayName("Falla cuando el curso no existe")
    void teacherUnassign_courseNoExist_unassignFail() {
        Long idCourse = 20L;
        Long idStudent = 45L;

        when(courseRepository.findById(idCourse)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            unassignTeacherToCourseService.unassign(idCourse, idStudent);
        });

        verify(teacherRepository, never()).findById(any());
    }

    private Course createCourse() {
        return new Course(20L, "Logica de programacion", "aspectos basicos", true, new Teacher(), new ArrayList<>());
    }

    private Teacher createTeacher() {
        return new Teacher(45L, "Pedro", "pedro@mail.com",  "Matematicas", new ArrayList<>());
    }
}