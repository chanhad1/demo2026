package com.example.demo2026.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo2026.entity.User;
import com.example.demo2026.repository.OrderRepository;
import com.example.demo2026.repository.ProductsRepository;
import com.example.demo2026.repository.UserRepository;

@Controller
public class HomeController {

    @Autowired
    private ProductsRepository productRepo;
    
    @Autowired
    private OrderRepository orderRepo;
    
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/home")
    public String home(Model model) {
        // Lấy user từ Spring Security context - luôn đúng, không bị lỗi session
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            System.out.println(">>> [HOME] Not authenticated, redirecting to login");
            return "redirect:/login";
        }

        User user = (User) authentication.getPrincipal();
        String role = user.getRole();
        System.out.println(">>> [HOME] User: " + user.getUsername() + ", Role: " + role);

        if ("ROLE_ADMIN".equals(role)) {
            // ===== GIAO DIỆN ADMIN =====
            try {
                model.addAttribute("productCount", productRepo.count());
                model.addAttribute("orderCount", orderRepo.count());
                model.addAttribute("userCount", userRepo.count());

                double totalRevenue = orderRepo.findAll()
                        .stream()
                        .mapToDouble(d -> d.getPrice() * d.getQuantity())
                        .sum();
                model.addAttribute("totalRevenue", totalRevenue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "admin/home"; // templates/admin/home.html

        } else if ("ROLE_CUSTOMER".equals(role)) {
            // ===== GIAO DIỆN KHÁCH HÀNG =====
            model.addAttribute("listSanPham", productRepo.findAll());
            return "shop/home"; // templates/shop/home.html

        } else {
            System.out.println(">>> [HOME] Unknown role: " + role + " -> redirect login");
            return "redirect:/login";
        }
    }

    @GetMapping("/home/products")
    public String products() { return "redirect:/products"; }

    @GetMapping("/home/cart")
    public String cart() { return "redirect:/cart"; }

    @GetMapping("/home/orders")
    public String orders() { return "redirect:/orders"; }

    @GetMapping("/home/stats")
    public String stats() { return "redirect:/stats"; }

    @GetMapping("/home/promo")
    public String promo() { return "redirect:/promo"; }
}