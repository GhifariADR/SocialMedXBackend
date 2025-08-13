package com.example.socialmedx.controller;

import com.example.socialmedx.dto.ApiResponse;
import com.example.socialmedx.dto.user.UserResponse;
import com.example.socialmedx.entity.User;
import com.example.socialmedx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/getAll")
    public ResponseEntity<?> getAllUser(){

        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponse = new ArrayList<>();

        for (User s : userList){
            UserResponse dto = new UserResponse(s.getId(), s.getUsername(),s.getEmail());
            userResponse.add(dto);
        }


        if (userList.isEmpty()){
            return ResponseEntity.ok(ApiResponse.error("User is empty",null));
        }

        return ResponseEntity.ok(ApiResponse.success("User Found",userResponse));
    }
}
