package com.example.demo.student.mapper;

import com.example.demo.course.Course;
import com.example.demo.course.dto.CourseSummaryResponse;
import com.example.demo.student.Student;
import com.example.demo.student.dto.StudentResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudentMapper {
    public StudentResponseDTO toStudentResponse(Student student) {
        List<CourseSummaryResponse> listCourses = new ArrayList<>();
        if(!student.getListCourses().isEmpty()) {
            for(Course c: student.getListCourses()) {
                listCourses.add(this.toCourseSummaryResponse(c));
            }
        }
        return new StudentResponseDTO(student.getId(), student.getName(), student.getMail(), listCourses);
    }

    private CourseSummaryResponse toCourseSummaryResponse(Course course) {
        return new CourseSummaryResponse(course.getId(), course.getName(), course.getDescription(), course.isStatus());
    }
}
