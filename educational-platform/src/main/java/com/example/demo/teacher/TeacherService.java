package com.example.demo.teacher;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.dto.TeacherResponseDTO;
import com.example.demo.teacher.dto.TeacherUpdateDTO;
import com.example.demo.teacher.mapper.TeacherMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService implements ITeacherService {
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;


    public TeacherService(TeacherRepository teacherRepository, TeacherMapper teacherMapper) {
        this.teacherRepository = teacherRepository;
        this.teacherMapper = teacherMapper;
    }

    @Override
    public Teacher saveTeacher(String name, String mail, String specialty) {
        if(this.teacherRepository.findTeacherEntityByMail(mail).isPresent()) {
            throw new ConflictException("El email ya esta registrado");
        }
        Teacher teacherNew = new Teacher();
        teacherNew.setName(name);
        teacherNew.setMail(mail);
        teacherNew.setSpecialty(specialty);
        return this.teacherRepository.save(teacherNew);
    }

    @Override
    public List<TeacherResponseDTO> findAllTeachers() {
        return this.teacherRepository.findAll().stream()
                .map(this.teacherMapper::toTeacherResponse)
                .toList();
    }

    @Override
    public TeacherResponseDTO findByIdTeacher(Long id) {
        return this.teacherMapper.toTeacherResponse(this.findByIdTeacherEntity(id));
    }

    @Override
    public void deleteByIdTeacher(Long id) {
        this.teacherRepository.deleteById(id);
    }

    @Override
    public TeacherResponseDTO updateTeacher(Long id, TeacherUpdateDTO teacherUpdateDTO) {
        Teacher updateTeacher = this.findByIdTeacherEntity(id);
        if(teacherUpdateDTO.getMail()!=null&&!teacherUpdateDTO.getMail().isBlank()) {
            if(this.teacherRepository.findTeacherEntityByMail(teacherUpdateDTO.getMail()).isPresent()) {
                throw new ConflictException("El email ya esta registrado");
            }
            updateTeacher.setMail(teacherUpdateDTO.getMail());
        }
        if(teacherUpdateDTO.getName()!=null&&!teacherUpdateDTO.getName().isBlank()) {
            updateTeacher.setName(teacherUpdateDTO.getName());
        }
        if(teacherUpdateDTO.getSpecialty()!=null&&!teacherUpdateDTO.getSpecialty().isBlank()) {
            updateTeacher.setSpecialty(teacherUpdateDTO.getSpecialty());
        }
        return this.teacherMapper.toTeacherResponse(this.teacherRepository.save(updateTeacher));
    }

    private Teacher findByIdTeacherEntity(Long id) {
        return this.teacherRepository.findById(id).orElseThrow(() -> new NotFoundException("Profesor no encontrado"));
    }
}
