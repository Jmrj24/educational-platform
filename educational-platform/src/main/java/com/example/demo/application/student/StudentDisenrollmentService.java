package com.example.demo.application.student;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.exception.NotFoundException;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StudentDisenrollmentService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public StudentDisenrollmentService(CourseRepository courseRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public void studentUnsubscribe(Long idCourse, Long idStudent) {
        Course course = this.courseRepository.findById(idCourse).orElseThrow(() -> new NotFoundException("El id del Curso no existe"));
        Student student = this.studentRepository.findById(idStudent).orElseThrow(() -> new NotFoundException("El id del Estudiante no existe"));

        student.getListCourses().remove(course);
        course.getListStudents().remove(student);
    }
}
