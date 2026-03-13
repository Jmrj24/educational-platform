package com.example.demo.userSec;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.role.RoleRepository;
import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.userSec.dto.UserSecResponseDTO;
import com.example.demo.userSec.dto.UserSecUpdateDTO;
import com.example.demo.userSec.mapper.UserSecMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSecServiceTest {
    @Mock
    private UserSecRepository userSecRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserSecMapper userSecMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserSecService userSecService;

    @Captor
    private ArgumentCaptor<UserSec> userSecCaptor;

    @Test
    @DisplayName("Lanza una excepcion si el username ingresado, ya esta registrado")
    void saveUserSec_usernameExits_runException() {
        UserSecRequestDTO userSecRequest = UserSecTestDataFactory.createUserSecRequest();
        Long idSubject = 999L;

        when(userSecRepository.findUserEntityByUsername(userSecRequest.username())).thenReturn(Optional.of(new UserSec()));

        assertThrows(ConflictException.class, () -> {
            userSecService.saveUserSec(userSecRequest, SubjectType.PROFESOR, Optional.of(idSubject));
        });

        verify(userSecRepository, never()).save(any());
    }

    @Test
    @DisplayName("Guarda al el usuario en la base de datos, si todo sale bien")
    void saveUserSec_userSecRequestValid_saveUserSecSuccessful() {
        UserSecRequestDTO userSecRequest = UserSecTestDataFactory.createUserSecRequest();
        Long idSubject = 999L;
        SubjectType subjectType = SubjectType.PROFESOR;
        UserSecResponseDTO userSecResponseExpect = UserSecTestDataFactory.createUserSecResponseFromRequest(userSecRequest, new HashSet<>(), subjectType, idSubject);

        when(passwordEncoder.encode(userSecRequest.password())).thenReturn(userSecRequest.password());
        when(userSecRepository.save(any(UserSec.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(userSecMapper.toUserSecResponse(any(UserSec.class))).thenReturn(userSecResponseExpect);

        UserSecResponseDTO userSecResponseResult = userSecService.saveUserSec(userSecRequest, subjectType, Optional.of(idSubject));

        verify(userSecRepository).save(userSecCaptor.capture());
        UserSec userSecSave = userSecCaptor.getValue();
        assertEquals(userSecRequest.username(), userSecSave.getUsername());
        assertEquals(userSecRequest.password(), userSecSave.getPassword());
        assertEquals(subjectType, userSecSave.getSubjectType());
        assertEquals(idSubject, userSecSave.getIdSubject());
        assertEquals(userSecResponseExpect, userSecResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un lista de usuarios DTO si existen")
    void findAllUserSecs_usersExist_returnUsers() {
        UserSec userSec = UserSecTestDataFactory.createUserSec();
        List<UserSec> listUsers = new ArrayList<>(List.of(userSec));
        UserSecResponseDTO userSecResponse = UserSecTestDataFactory.createUserSecResponse(userSec, new HashSet<>());
        Set<UserSecResponseDTO> listUserResponseExpect = new HashSet<>(List.of(userSecResponse));

        when(userSecRepository.findAll()).thenReturn(listUsers);
        when(userSecMapper.toUserSecResponse(userSec)).thenReturn(userSecResponse);

        Set<UserSecResponseDTO> listUsersResponseResult = userSecService.findAllUserSecs();

        assertEquals(listUserResponseExpect, listUsersResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un lista vacia si no hay registro de Usuarios")
    void findAllUserSecs_usersNoExist_returnListEmpty() {
        when(userSecRepository.findAll()).thenReturn(Collections.emptyList());

        Set<UserSecResponseDTO> listUsersResponseResult = userSecService.findAllUserSecs();

        assertNotNull(listUsersResponseResult);
        assertTrue(listUsersResponseResult.isEmpty());
    }

    @Test
    @DisplayName("Debe devolver un usuario si el Id existe")
    void findByIdUser_userExist_returnUserSecDTO() {
        Long id = 258L;
        UserSec userSec = UserSecTestDataFactory.createUserSec();
        UserSecResponseDTO userSecExpected = UserSecTestDataFactory.createUserSecResponse(userSec, Collections.emptySet());

        when(userSecRepository.findById(id)).thenReturn(Optional.of(userSec));
        when(userSecMapper.toUserSecResponse(userSec)).thenReturn(userSecExpected);

        UserSecResponseDTO userSecResult = userSecService.findByIdUserSec(id);

        assertEquals(userSecExpected, userSecResult);
        verify(userSecRepository).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar una excepcion al no existir el id del usuario")
    void findByIdUser_userNoExist_runException() {
        Long id = 1L;

        when(userSecRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userSecService.findByIdUserSec(id);
        });
    }

    @Test
    @DisplayName("Debe lanzar una exception si no existe el ID")
    void updateUser_userNoExist_runException() {
        Long idUser = 4L;
        UserSecUpdateDTO userSecUpdate = this.createUserUpdate();

        when(userSecRepository.findById(idUser)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userSecService.updateUserSec(idUser, userSecUpdate);
        });

        verify(userSecRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar el usuario si todo sale bien")
    void updateUser_userExist_userSecUpdateSuccessful() {
        Long idUser = 258L;
        UserSec userSec = UserSecTestDataFactory.createUserSec();
        UserSecUpdateDTO userSecUpdate = this.createUserUpdate();

        when(userSecRepository.findById(idUser)).thenReturn(Optional.of(userSec));
        when(passwordEncoder.encode(userSecUpdate.getPassword())).thenReturn(userSecUpdate.getPassword());
        when(userSecRepository.save(userSec)).thenReturn(userSec);
        UserSecResponseDTO userResponseExpected = UserSecTestDataFactory.createUserSecResponse(userSec, Collections.emptySet());
        when(userSecMapper.toUserSecResponse(userSec)).thenReturn(userResponseExpected);

        UserSecResponseDTO userResponseResult = userSecService.updateUserSec(idUser, userSecUpdate);

        assertEquals(userSec.getUsername(), userSecUpdate.getUsername());
        assertEquals(userSec.getPassword(), userSecUpdate.getPassword());
        assertEquals(userSec.isEnabled(), userSecUpdate.getEnabled());
        assertEquals(userSec.isAccountNotExpired(), userSecUpdate.getAccountNotExpired());
        assertEquals(userSec.isAccountNotLocked(), userSecUpdate.getAccountNotLocked());
        assertEquals(userSec.isCredentialNotExpired(), userSecUpdate.getCredentialNotExpired());
        verify(userSecRepository).save(userSec);
        assertEquals(userResponseExpected, userResponseResult);
    }

    private UserSecUpdateDTO createUserUpdate() {
        return new UserSecUpdateDTO(
                "Nuevo username",
                "1234",
                false,
                false,
                false,
                false,
                new HashSet<>());
    }
}