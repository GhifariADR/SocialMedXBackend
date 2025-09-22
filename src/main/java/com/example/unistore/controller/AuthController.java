package com.example.unistore.controller;

import com.example.unistore.dto.ApiResponse;
import com.example.unistore.dto.auth.LoginRequest;
import com.example.unistore.dto.auth.RegisterRequest;
import com.example.unistore.entity.Cart;
import com.example.unistore.entity.Role;
import com.example.unistore.entity.User;
import com.example.unistore.entity.UserToken;
import com.example.unistore.repository.CartRepository;
import com.example.unistore.repository.RoleRepository;
import com.example.unistore.repository.UserRepository;
import com.example.unistore.repository.UserTokenRepository;
import com.example.unistore.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private long jwtExpiration;



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
        Cart newCart = new Cart();

        Optional<Role> userRole = roleRepository.findByName("user");

        if (!userRole.isPresent()) {
            return ResponseEntity.ok(ApiResponse.error("Role not found", null));
        }

        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setRole(userRole.get());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(newUser);

        newCart.setUser(newUser);
        newCart.setCreatedAt(new Date());
        newCart.setUpdatedAt(new Date());

        cartRepository.save(newCart);

        return ResponseEntity.ok(ApiResponse.success("User registered successfully", null));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequest request){

        Date now = new Date();
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Map<String, String> responseToken = new HashMap<>();

        if(!userOpt.isPresent() || !encoder.matches(request.getPassword(), userOpt.get().getPassword())){
            return ResponseEntity.ok(ApiResponse.error("Invalid Credential", null));
        }

        Long userId = userOpt.get().getId();
//        Optional<UserToken> userTokenOpt = userTokenRepository.findByUser_IdAndRevokeFalseAndExpiredAtAfter(userId, now);

        Optional<UserToken> userTokenOpt = userTokenRepository.findByUser_Id(userId);

        if(userTokenOpt.isPresent()){
            if (userTokenOpt.get().isRevoke()){
                return ResponseEntity.ok(ApiResponse.error("User is not active", null));
            }

            if(userTokenOpt.get().getExpiredAt().before(now)){
                userTokenRepository.delete(userTokenOpt.get());

            }

            responseToken.put("token", userTokenOpt.get().getToken());
            return ResponseEntity.ok(ApiResponse.success("User login successfully", responseToken));
        }

        UserToken userToken = new UserToken();
        String token = jwtUtil.generateToken(request.getUsername(), userOpt.get().getRole().getName());
        responseToken.put("token", token);

        userToken.setToken(token);
        userToken.setCreatedAt(now);
        userToken.setExpiredAt(new Date(now.getTime() + jwtExpiration));
        userToken.setRevoke(false);
        userToken.setUser(userOpt.get());

        userTokenRepository.save(userToken);


        return ResponseEntity.ok(ApiResponse.success("User login successfully", responseToken));

    }
}
