package com.example.socialmedx.controller;

import com.example.socialmedx.dto.ApiResponse;
import com.example.socialmedx.entity.Follow;
import com.example.socialmedx.entity.User;
import com.example.socialmedx.repository.FollowRepository;
import com.example.socialmedx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @PostMapping("/{followerId}/follow/{followedId}")
    public ResponseEntity<?> followUser(@PathVariable Long followerId, @PathVariable Long followedId){

        Optional<User> userFollower = userRepository.findById(followerId);
        Optional<User> userFollowed = userRepository.findById(followedId);

        if (!userFollowed.isPresent() || !userFollower.isPresent()){
            return ResponseEntity.ok(ApiResponse.error("Follower or followed not found",null));
        }

        if(followedId.equals(followerId)){
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
}
