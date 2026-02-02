package com.example.demo.security.auth;

import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.security.auth.dto.AuthLoginRequestDTO;
import com.example.demo.security.auth.dto.AuthResponseDTO;
import com.example.demo.security.userDetails.UserDetailsServiceImp;
import com.example.demo.utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserDetailsServiceImp userDetailsServiceImp;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserDetailsServiceImp userDetailsServiceImp, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userDetailsServiceImp = userDetailsServiceImp;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO loginUser (AuthLoginRequestDTO authLoginRequest) {
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate (username, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.createToken(authentication);
        return new AuthResponseDTO(username, "login ok", accessToken, true);
    }

    public Authentication authenticate (String username, String password) {
        UserDetails userDetails = this.userDetailsServiceImp.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new InvalidCredentialsException("Usuario o Password invalidos");
        }
        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
