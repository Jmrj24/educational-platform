package com.example.demo.integration.student;

import com.example.demo.role.Role;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.example.demo.student.StudentTestDataFactory;
import com.example.demo.student.dto.StudentRequestDTO;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import com.example.demo.userSec.UserSecTestDataFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMINISTRADOR")
@ActiveProfiles("test")
public class CreateStudentAccountIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserSecRepository userSecRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Crea el estudiante y su cuenta asociada, si todo sale bien")
    @Transactional
    void saveStudent_studentRequestValid_return201() throws Exception {
        StudentRequestDTO studentRequest = StudentTestDataFactory.createValidRequest();
        studentRequest.rolesListIds().add(1L); // Rol usado, es el creado por el bootstrap

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isCreated());

        Student studentSaved = studentRepository.findStudentEntityByMail(studentRequest.mail()).orElseThrow();
        UserSec userSecSaved = userSecRepository.findUserEntityByUsername(studentRequest.username()).orElseThrow();

        assertEquals(studentRequest.name(), studentSaved.getName());
        assertEquals(studentRequest.mail(), studentSaved.getMail());
        assertEquals(SubjectType.ESTUDIANTE, userSecSaved.getSubjectType());
        assertEquals(studentSaved.getId(), userSecSaved.getIdSubject());
        assertTrue(passwordEncoder.matches(studentRequest.password(), userSecSaved.getPassword()));
        assertTrue(
                userSecSaved.getRolesList().stream()
                        .map(Role::getId)
                        .anyMatch(r -> r.equals(1L))
        );
    }

    @Test
    @DisplayName("Lanza una excepcion al ingresar un mail de estudiate ya registrado")
    @Transactional
    void saveStudent_studentMailRepeat_return409() throws Exception {
        studentRepository.save(StudentTestDataFactory.createStudentFromIntegration());
        Long studentCountBefore = studentRepository.count();
        Long userCountBefore = userSecRepository.count();
        StudentRequestDTO studentRequest = StudentTestDataFactory.createValidRequest();
        studentRequest.rolesListIds().add(1L);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isConflict());

        assertEquals(studentCountBefore, studentRepository.count());
        assertEquals(userCountBefore, userSecRepository.count());
    }

    @Test
    @DisplayName("Lanza una excepcion al ingresar un username de estudiate ya registrado")
    void saveStudent_usernameRepeat_return409() throws Exception {
        StudentRequestDTO studentRequest = StudentTestDataFactory.createValidRequest();
        studentRequest.rolesListIds().add(1L);
        UserSec userSec = UserSecTestDataFactory.createUserSecFromIntegration();
        userSec.setUsername(studentRequest.username());
        userSecRepository.save(userSec);
        Long studentCountBefore = studentRepository.count();
        Long userCountBefore = userSecRepository.count();

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isConflict());

        assertEquals(studentCountBefore, studentRepository.count());
        assertEquals(userCountBefore, userSecRepository.count());

        studentRepository.deleteAll();
        userSecRepository.deleteAll();
    }
}