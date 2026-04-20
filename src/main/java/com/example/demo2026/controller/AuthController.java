package com.example.demo2026.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo2026.entity.User;
import com.example.demo2026.repository.UserRepository;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    // ================= LOGIN =================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    // Spring Security handles @PostMapping("/login") automatically

    // ================= REGISTER =================

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {

        // kiểm tra trùng username
        if (userRepo.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Tài khoản đã tồn tại!");
            return "register";
        }

        user.setRole("ROLE_CUSTOMER");
        userRepo.save(user);
        return "redirect:/"; // Chuyển về trang đăng nhập sau khi đăng ký
    }

    // Spring Security handles @GetMapping("/logout") automatically
}