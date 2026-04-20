package com.example.demo2026.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo2026.entity.User;
import com.example.demo2026.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng: " + username);
        }
        return user;
    }

    // 👉 REGISTER
    public boolean register(User user) {
        // check trùng username
        if (userRepo.existsByUsername(user.getUsername())) {
            return false;
        }
        userRepo.save(user);
        return true;
    }
}