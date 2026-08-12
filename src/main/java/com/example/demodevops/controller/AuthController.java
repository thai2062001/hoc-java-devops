package com.example.demodevops.controller;

import com.example.demodevops.dto.ApiResponse;
import com.example.demodevops.dto.JwtResponse;
import com.example.demodevops.dto.LoginRequest;
import com.example.demodevops.security.EmployeePrincipal;
import com.example.demodevops.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Thực hiện xác thực thông qua email và password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // Đăng ký thông tin xác thực vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Sinh mã token JWT
        String jwt = tokenProvider.generateToken(authentication);

        // Lấy thông tin user principal
        EmployeePrincipal userPrincipal = (EmployeePrincipal) authentication.getPrincipal();
        String role = userPrincipal.getEmployee().getRole() != null ? 
                userPrincipal.getEmployee().getRole().getCode() : "NONE";

        JwtResponse jwtResponse = new JwtResponse(
                jwt,
                userPrincipal.getUsername(),
                userPrincipal.getEmployee().getFullName(),
                role
        );

        return ResponseEntity.ok(ApiResponse.success(jwtResponse, "Login successful"));
    }
}
