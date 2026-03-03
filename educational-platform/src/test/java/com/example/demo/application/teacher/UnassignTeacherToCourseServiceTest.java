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
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        Course course = CourseTestDataFactory.createCourse();
        teacher.getListCourses().add(course);
        course.setStatus(true);
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
        Course course = new Course();

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
}