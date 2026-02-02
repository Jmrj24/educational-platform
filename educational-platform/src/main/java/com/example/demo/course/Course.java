package com.example.demo.course;

import com.example.demo.student.Student;
import com.example.demo.teacher.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacherCourse;
    @ManyToMany(mappedBy = "listCourses")
    private List<Student> listStudents = new ArrayList<>();
}
