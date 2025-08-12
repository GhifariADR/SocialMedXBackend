package com.example.socialmedx.controller;

import com.example.socialmedx.dto.ApiResponse;
import com.example.socialmedx.dto.auth.LoginRequest;
import com.example.socialmedx.dto.auth.RegisterRequest;
import com.example.socialmedx.entity.User;
import com.example.socialmedx.repository.UserRepository;
import com.example.socialmedx.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;



    @PostMapping("/register")
    public ResponseEntity<?> registerUser (@RequestBody RegisterRequest request){
        Optional<User> usersOptional = userRepository.findByUsername(request.getUsername());

        if (usersOptional.isPresent()){
            return ResponseEntity.ok(ApiResponse.error("Username already exists", null));
        }

        if(userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.ok(ApiResponse.error("Email already exists", null));
        }

        User newUser = new User();

        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(newUser);

        return ResponseEntity.ok(ApiResponse.success("User registered successfully", null));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequest request){
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            String jwt = jwtUtil.generateToken(request.getUsername());

            Map<String,String> tokenResponse = new HashMap<>();
            tokenResponse.put("token", jwt);

            return ResponseEntity.ok(ApiResponse.success("Login successfully", tokenResponse));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid Credentials", null));
        }
    }
}
