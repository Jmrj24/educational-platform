package com.example.demo.teacher.mapper;

import com.example.demo.course.Course;
import com.example.demo.course.dto.CourseSummaryResponse;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.dto.TeacherResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TeacherMapper {
    public TeacherResponseDTO toTeacherResponse(Teacher teacher) {
        List<CourseSummaryResponse> listCourses = new ArrayList<>();
        if(!teacher.getListCourses().isEmpty()) {
            for(Course c: teacher.getListCourses()) {
                listCourses.add(this.toCourseSummaryResponse(c));
            }
        }
        return new TeacherResponseDTO(teacher.getId(), teacher.getName(), teacher.getMail(), teacher.getSpecialty(),
                listCourses);
    }

    private CourseSummaryResponse toCourseSummaryResponse(Course course) {
        return new CourseSummaryResponse(course.getId(), course.getName(), course.getDescription(), course.isStatus());
    }
}
