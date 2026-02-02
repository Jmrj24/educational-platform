package com.example.demo.application.teacher;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AssignTeacherToCourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    public AssignTeacherToCourseService(CourseRepository courseRepository, TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    public void assign(Long idCourse, Long idTeacher) {
        Course course = this.courseRepository.findById(idCourse).orElseThrow(() -> new NotFoundException("El id del Curso no existe"));
        Teacher teacher = this.teacherRepository.findById(idTeacher).orElseThrow(() -> new NotFoundException("El id del Profesor no existe"));

        teacher.getListCourses().add(course);
        course.setTeacherCourse(teacher);
        course.setStatus(true);
    }
}
