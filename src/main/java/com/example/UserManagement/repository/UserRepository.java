package com.example.UserManagement.repository;

import com.example.UserManagement.dto.UserResponse;
import com.example.UserManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String Email);
    Boolean existsByEmail(String email);
}
