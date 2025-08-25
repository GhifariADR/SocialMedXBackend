package com.example.socialmedx.repository;

import com.example.socialmedx.entity.Follow;
import com.example.socialmedx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

}
