package com.example.demo.student;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.student.dto.*;
import com.example.demo.student.mapper.StudentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService implements IStudentService{
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    public Student saveStudent(String name, String mail) {
        if(this.studentRepository.findStudentEntityByMail(mail).isPresent()) {
            throw new ConflictException("El email ya esta registrado");
        }
        Student studentNew = new Student();
        studentNew.setName(name);
        studentNew.setMail(mail);
        return this.studentRepository.save(studentNew);
    }

    @Override
    public List<StudentResponseDTO> findAllStudents() {
        return this.studentRepository.findAll().stream()
                .map(this.studentMapper::toStudentResponse)
                .toList();
    }

    @Override
    public StudentResponseDTO findByIdStudent(Long id) {
        return this.studentMapper.toStudentResponse(this.findByIdStudentEntity(id));
    }

    @Override
    public void deleteByIdStudent(Long id) {
        this.studentRepository.deleteById(id);
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentUpdateDTO studentUpdateDTO) {
        Student updateStudent = this.findByIdStudentEntity(id);
        if(studentUpdateDTO.getMail()!=null&&!studentUpdateDTO.getMail().isBlank()) {
            if(this.studentRepository.findStudentEntityByMail(studentUpdateDTO.getMail()).isPresent()) {
                throw new ConflictException("El email ya esta registrado");
            }
            updateStudent.setMail(studentUpdateDTO.getMail());
        }
        if(studentUpdateDTO.getName()!=null&&!studentUpdateDTO.getName().isBlank()) {
            updateStudent.setName(studentUpdateDTO.getName());
        }
        return this.studentMapper.toStudentResponse(this.studentRepository.save(updateStudent));
    }

    private Student findByIdStudentEntity(Long id) {
        return this.studentRepository.findById(id).orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));
    }
}
