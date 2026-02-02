package com.example.demo.userSec;

import com.example.demo.role.RoleRepository;
import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.userSec.dto.UserSecResponseDTO;
import com.example.demo.userSec.dto.UserSecUpdateDTO;
import com.example.demo.userSec.mapper.UserSecMapper;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.role.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserSecService implements IUserSecService {
    private final UserSecRepository userSecRepository;
    private final RoleRepository roleRepository;
    private final UserSecMapper userSecMapper;
    private final PasswordEncoder passwordEncoder;


    public UserSecService(UserSecRepository userSecRepository, RoleRepository roleRepository,
                          UserSecMapper userSecMapper, PasswordEncoder passwordEncoder) {
        this.userSecRepository = userSecRepository;
        this.roleRepository = roleRepository;
        this.userSecMapper = userSecMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserSecResponseDTO saveUserSec(UserSecRequestDTO userSecRequestDTO, SubjectType subjectType, Optional<Long> idSubject) {
        if(this.userSecRepository.findUserEntityByUsername(userSecRequestDTO.username()).isPresent()) {
            throw new ConflictException("El Username ya esta registrado");
        }
        UserSec userNew = new UserSec();
        userNew.setUsername(userSecRequestDTO.username());
        userNew.setPassword(encryptPassword(userSecRequestDTO.password()));
        userNew.setEnabled(userSecRequestDTO.enabled());
        userNew.setAccountNotExpired(userSecRequestDTO.accountNotExpired());
        userNew.setAccountNotLocked(userSecRequestDTO.accountNotLocked());
        userNew.setCredentialNotExpired(userSecRequestDTO.credentialNotExpired());
        userNew.setRolesList(this.findRoles(userSecRequestDTO.rolesListIds()));
        userNew.setSubjectType(subjectType);
        idSubject.ifPresent(userNew::setIdSubject);
        return this.userSecMapper.toUserSecResponse(this.userSecRepository.save(userNew));
    }

    @Override
    public Set<UserSecResponseDTO> findAllUserSecs() {
        return this.userSecRepository.findAll().stream()
                .map(this.userSecMapper::toUserSecResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public UserSecResponseDTO findByIdUserSec(Long id) {
        return this.userSecMapper.toUserSecResponse(this.findByIdUserSecEntity(id));
    }

    @Override
    public void deleteByIdUserSec(Long id) {
        this.userSecRepository.deleteById(id);
    }

    @Override
    public UserSecResponseDTO updateUserSec(Long id, UserSecUpdateDTO userSecUpdateDTO) {
        UserSec updateUser = this.findByIdUserSecEntity(id);
        if(userSecUpdateDTO.getUsername()!=null&&!userSecUpdateDTO.getUsername().isBlank()) {
            updateUser.setUsername(userSecUpdateDTO.getUsername());
        }
        if(userSecUpdateDTO.getPassword()!=null&&!userSecUpdateDTO.getPassword().isBlank()) {
            updateUser.setPassword(encryptPassword(userSecUpdateDTO.getPassword()));
        }
        if(userSecUpdateDTO.getEnabled()!=null) {
            updateUser.setEnabled(userSecUpdateDTO.getEnabled());
        }
        if(userSecUpdateDTO.getAccountNotExpired()!=null) {
            updateUser.setAccountNotExpired(userSecUpdateDTO.getAccountNotExpired());
        }
        if(userSecUpdateDTO.getAccountNotLocked()!=null) {
            updateUser.setAccountNotLocked(userSecUpdateDTO.getAccountNotLocked());
        }
        if(userSecUpdateDTO.getCredentialNotExpired()!=null) {
            updateUser.setCredentialNotExpired(userSecUpdateDTO.getCredentialNotExpired());
        }
        if(userSecUpdateDTO.getRolesListIds()!=null&&!userSecUpdateDTO.getRolesListIds().isEmpty()) {
            updateUser.setRolesList(this.findRoles(userSecUpdateDTO.getRolesListIds()));
        }
        return this.userSecMapper.toUserSecResponse(this.userSecRepository.save(updateUser));
    }

    private UserSec findByIdUserSecEntity(Long id) {
        return this.userSecRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no Encontrado"));
    }

    private Set<Role> findRoles(Set<Long> rolesListIds) {
        Set<Role> rolesList = new HashSet<>();
        for(Long roleId:rolesListIds) {

            rolesList.add(this.roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Rol no Encontrado")));
        }
        return rolesList;
    }

    private String encryptPassword(String password) {
        return this.passwordEncoder.encode(password);
    }
}
