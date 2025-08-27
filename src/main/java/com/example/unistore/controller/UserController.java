package com.example.unistore.controller;

import com.example.unistore.dto.ApiResponse;
import com.example.unistore.dto.user.UpdateProfileRequest;
import com.example.unistore.dto.user.UpdateUserRequest;
import com.example.unistore.dto.user.UserResponse;
import com.example.unistore.entity.User;
import com.example.unistore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateUser (@PathVariable Long id, @RequestBody UpdateUserRequest request){

        Optional<User> userOpt = userRepository.findById(id);

        if (!userOpt.isPresent()){
            return ResponseEntity.ok(ApiResponse.error("User not found",null));
        }

        User user = userOpt.get();

        if (!request.getUsername().isEmpty() && request.getUsername() != null){
            user.setUsername(request.getUsername());
        }

        if (!request.getEmail().isEmpty() && request.getEmail() != null){
            user.setEmail(request.getEmail());
        }



        userRepository.save(user);


        return ResponseEntity.ok(ApiResponse.success("Successfully edit user",null));
    }

    @PostMapping("/updateProfile/{id}")
    public ResponseEntity<?> updateProfile (@PathVariable Long id, @RequestBody UpdateProfileRequest request){

        Optional<User> userOpt = userRepository.findById(id);

        if (!userOpt.isPresent()){
            return ResponseEntity.ok(ApiResponse.error("User not found",null));
        }

        User user = userOpt.get();

        if (!request.getBio().isEmpty() && request.getBio() != null){
            user.setBio(request.getBio());
        }

        if (!request.getProfilePicture().isEmpty() && request.getProfilePicture() != null){
            user.setProfilePicture(request.getProfilePicture());
        }

        userRepository.save(user);


        return ResponseEntity.ok(ApiResponse.success("Successfully edit profile",null));
    }


}
