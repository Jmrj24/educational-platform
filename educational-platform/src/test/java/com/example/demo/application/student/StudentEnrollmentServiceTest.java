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
import static org.mockito.Mockito.*;

public class StudentEnrollmentServiceTest {
    private final CourseRepository courseRepository = Mockito.mock(CourseRepository.class);
    private final StudentRepository studentRepository = Mockito.mock(StudentRepository.class);

    private final StudentEnrollmentService studentEnrollmentService = new StudentEnrollmentService(courseRepository, studentRepository);

    @Test
    @DisplayName("Debe lanzar una excepcion si el curso no existe")
    void studentInscription_courseNoExist_runException() {
        Long idCourse = 20L;
        Long idStudent = 45L;

        when(courseRepository.findById(idCourse)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            studentEnrollmentService.studentInscription(idCourse, idStudent);
        });

        verify(studentRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe lanzar una excepcion si el estudiante no existe")
    void studentInscription_studentNoExist_runException() {
        Long idCourse = 20L;
        Long idStudent = 45L;
        Course course = this.createCourse();

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(studentRepository.findById(idStudent)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            studentEnrollmentService.studentInscription(idCourse, idStudent);
        });
    }

    @Test
    @DisplayName("Debe inscribir al estudiante en el curso")
    void studentInscription_courseAndStudentExist_inscriptionSuccess() {
        Long idCourse = 20L;
        Long idStudent = 45L;
        Student student = this.createStudent();
        Course course = this.createCourse();

        when(studentRepository.findById(idStudent)).thenReturn(Optional.of(student));
        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));

        studentEnrollmentService.studentInscription(idCourse, idStudent);

        assertTrue(student.getListCourses().contains(course));
        assertTrue(course.getListStudents().contains(student));
    }

    private Course createCourse() {
        return new Course(20L, "Logica de programacion", "aspectos basicos", true, new Teacher(), new ArrayList<>());
    }

    private Student createStudent() {
        return new Student(45L, "Pedro", "pedro@mail.com", new ArrayList<>());
    }
}
