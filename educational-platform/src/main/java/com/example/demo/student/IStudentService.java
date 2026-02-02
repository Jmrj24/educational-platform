package com.example.demo.student;

import com.example.demo.student.dto.*;

import java.util.List;

public interface IStudentService {
    Student saveStudent(String name, String mail);
    List<StudentResponseDTO> findAllStudents();
    StudentResponseDTO findByIdStudent(Long id);
    void deleteByIdStudent(Long id);
    StudentResponseDTO updateStudent(Long id, StudentUpdateDTO studentUpdateDTO);
}