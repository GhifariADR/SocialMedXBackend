package com.example.socialmedx.controller;

import com.example.socialmedx.dto.ApiResponse;
import com.example.socialmedx.entity.Follow;
import com.example.socialmedx.entity.User;
import com.example.socialmedx.repository.FollowRepository;
import com.example.socialmedx.repository.UserRepository;
import com.example.socialmedx.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class FollowController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/follow/{followedId}")
    public ResponseEntity<?> followUser(@PathVariable Long followedId, HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ") || followedId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Missing or invalid token", null));
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Optional<User> userFollower = userRepository.findByUsername(username);
        Optional<User> userFollowed = userRepository.findById(followedId);

        if (!userFollowed.isPresent() || !userFollower.isPresent()){
            return ResponseEntity.ok(ApiResponse.error("Follower or followed not found",null));
        }

        if(userFollower.get() == userFollowed.get()){
            return ResponseEntity.ok(ApiResponse.error("You can't follow yourself",null));
        }

        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(userFollower.get(), userFollowed.get());

        if (existingFollow.isPresent()){
            return ResponseEntity.ok(ApiResponse.error("Already follow",null));
        }

        Follow follow = new Follow();
        follow.setFollower(userFollower.get());
        follow.setFollwed(userFollowed.get());
        follow.setCreatedAt(new Date());

        followRepository.save(follow);

        return ResponseEntity.ok(ApiResponse.success("Follow successfully",null));

    }

    @PostMapping("/unfollow/{followedId}")
    public ResponseEntity<?> unFollow (@PathVariable Long followedId, HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ") || followedId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Missing or invalid token", null));
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Optional<User> userFollower = userRepository.findByUsername(username);
        Optional<User> userFollowed = userRepository.findById(followedId);

        if (!userFollowed.isPresent() || !userFollower.isPresent()){
            return ResponseEntity.ok(ApiResponse.error("Follower or followed not found",null));
        }

        if(userFollower.get() == userFollowed.get()){
            return ResponseEntity.ok(ApiResponse.error("You can't unfollow yourself",null));
        }

        Optional<Follow> existingFollow = followRepository.findByFollowerAndFollowed(userFollower.get(), userFollowed.get());

        if (!existingFollow.isPresent()){
            return ResponseEntity.ok(ApiResponse.error("not Found",null));
        }

        followRepository.delete(existingFollow.get());

        return ResponseEntity.ok(ApiResponse.success("Successfully unfollow user",null));

    }

}
