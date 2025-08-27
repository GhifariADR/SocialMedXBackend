package com.example.unistore.repository;

import com.example.unistore.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByTokenAndRevokeFalse(String token);

    Optional<UserToken> findByUser_Id(Long id);

    Optional<UserToken> findByUser_IdAndRevokeFalseAndExpiredAtAfter(Long userId, Date now);


}
