package com.example.demo.security.userDetails;

import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserSecRepository userSecRepository;

    public UserDetailsServiceImp(UserSecRepository userSecRepository) {
        this.userSecRepository = userSecRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserSec userSec = this.userSecRepository.findUserEntityByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Usuario o Password invalidos"));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        

        userSec.getRolesList()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole().name()))));

        userSec.getRolesList().stream()
                .flatMap(role -> role.getPermissionsList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName())));

        return new User(
                userSec.getUsername(),
                userSec.getPassword(),
                userSec.isEnabled(),
                userSec.isAccountNotExpired(),
                userSec.isCredentialNotExpired(),
                userSec.isAccountNotLocked(),
                authorityList);
    }
}
