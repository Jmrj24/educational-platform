package com.example.demo.application.student;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.exception.NotFoundException;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.example.demo.teacher.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StudentDisenrollmentServiceTest {
    private final CourseRepository courseRepository = Mockito.mock(CourseRepository.class);
    private final StudentRepository studentRepository = Mockito.mock(StudentRepository.class);

    private final StudentDisenrollmentService studentDisenrollmentService = new StudentDisenrollmentService(courseRepository, studentRepository);

    @Test
    @DisplayName("Elimina la suscripcion del estudiante en el curso")
    void studentUnsubscribe_studentAndCourseExist_unsubscribeExit() {
        Long idCourse = 20L;
        Long idStudent = 45L;
        Student student = this.createStudent();
        Course course = this.createCourse();
        student.getListCourses().add(course);
        course.getListStudents().add(student);

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(studentRepository.findById(idStudent)).thenReturn(Optional.of(student));

        studentDisenrollmentService.studentUnsubscribe(idCourse, idStudent);

        assertFalse(student.getListCourses().contains(course));
        assertFalse(course.getListStudents().contains(student));
    }

    @Test
    @DisplayName("Falla cuando el estudiante no existe")
    void studentUnsubscribe_studentNoExist_unsubscribeFail() {
        Long idCourse = 20L;
        Long idStudent = 45L;
        Course course = this.createCourse();

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(studentRepository.findById(idStudent)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
                studentDisenrollmentService.studentUnsubscribe(idCourse, idStudent);
        });
    }

    @Test
    @DisplayName("Falla cuando el curso no existe")
    void studentUnsubscribe_courseNoExist_unsubscribeFail() {
        Long idCourse = 20L;
        Long idStudent = 45L;

        when(courseRepository.findById(idCourse)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            studentDisenrollmentService.studentUnsubscribe(idCourse, idStudent);
        });

        verify(studentRepository, never()).findById(any());
    }

    private Student createStudent() {
        return new Student(45L, "Pedro", "pedro@mail.com", new ArrayList<>());
    }

    private Course createCourse() {
        return new Course(20L, "Logica de programacion", "aspectos basicos", true, new Teacher(), new ArrayList<>());
    }
}
