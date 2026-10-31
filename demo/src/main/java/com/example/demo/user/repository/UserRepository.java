package com.example.demo.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // 아이디로 회원 조회
    Optional<User> findByUsername(String username);

    // 이메일로 회원 조회
    Optional<User> findByEmail(String email);

    // 아이디 중복 확인
    boolean existsByUsername(String username);

    // 이메일 중복 확인
    boolean existsByEmail(String email);
}
