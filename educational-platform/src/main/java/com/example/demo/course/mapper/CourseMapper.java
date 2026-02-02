package com.example.demo.course.mapper;

import com.example.demo.course.Course;
import com.example.demo.course.dto.CourseResponseDTO;
import com.example.demo.student.Student;
import com.example.demo.student.dto.StudentSummaryResponse;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.dto.TeacherSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourseMapper {
    public CourseResponseDTO toCourseResponse(Course course) {
        TeacherSummaryResponse teacherResponse = course.getTeacherCourse() != null
                ? this.toTeacherSummaryResponse(course.getTeacherCourse())
                : null;

        List<StudentSummaryResponse> listStudents = new ArrayList<>();
        if (!course.getListStudents().isEmpty()) {
            for(Student s: course.getListStudents()) {
                listStudents.add(this.toStudentSummaryResponse(s));
            }
        }
        return new CourseResponseDTO(course.getId(), course.getName(), course.getDescription(), course.isStatus(),
                teacherResponse, listStudents);
    }

    private TeacherSummaryResponse toTeacherSummaryResponse(Teacher teacher) {
        return new TeacherSummaryResponse(teacher.getId(), teacher.getName(), teacher.getMail(), teacher.getSpecialty());
    }

    private StudentSummaryResponse toStudentSummaryResponse(Student student) {
        return new StudentSummaryResponse(student.getId(), student.getName(), student.getMail());
    }
}
