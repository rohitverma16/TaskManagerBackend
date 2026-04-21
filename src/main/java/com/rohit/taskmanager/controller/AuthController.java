package com.rohit.taskmanager.controller;

import com.rohit.taskmanager.dto.user.LoginRequestDto;
import com.rohit.taskmanager.dto.user.UserRequestDto;
import com.rohit.taskmanager.entity.User;
import com.rohit.taskmanager.jwt.JwtUtil;
import com.rohit.taskmanager.service.CustomeUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomeUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomeUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginRequestDto loginRequestDto) {
        authenticationManager.
                authenticate( new UsernamePasswordAuthenticationToken(
                        loginRequestDto.username(), loginRequestDto.password()));
        User user = userDetailsService.findByUsername(loginRequestDto.username());
        String token = jwtUtil.generateToken(user.getUsername(),user.getRole().name());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody UserRequestDto userRequestDto) {
        userDetailsService.save(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

}
