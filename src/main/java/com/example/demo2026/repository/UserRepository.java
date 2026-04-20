package com.example.demo2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo2026.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // 👉 Tìm theo username (login)
    User findByUsername(String username);

    // 👉 Kiểm tra tồn tại (register)
    boolean existsByUsername(String username);
}