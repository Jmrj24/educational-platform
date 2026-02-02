package com.example.demo.teacher;

import com.example.demo.teacher.dto.TeacherResponseDTO;
import com.example.demo.teacher.dto.TeacherUpdateDTO;

import java.util.List;

public interface ITeacherService {
    Teacher saveTeacher(String name, String mail, String specialty);
    List<TeacherResponseDTO> findAllTeachers();
    TeacherResponseDTO findByIdTeacher(Long id);
    void deleteByIdTeacher(Long id);
    TeacherResponseDTO updateTeacher(Long id, TeacherUpdateDTO teacherUpdateDTO);
}
