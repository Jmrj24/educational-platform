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
public class UnassignTeacherToCourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    public UnassignTeacherToCourseService(CourseRepository courseRepository, TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    public void unassign(Long idCourse, Long idTeacher) {
        Course course = this.courseRepository.findById(idCourse).orElseThrow(() -> new NotFoundException("El id del Curso no existe"));
        Teacher teacher = this.teacherRepository.findById(idTeacher).orElseThrow(() -> new NotFoundException("El id del Profesor no existe"));

        teacher.getListCourses().remove(course);
        course.setTeacherCourse(null);
        course.setStatus(false);
    }
}
