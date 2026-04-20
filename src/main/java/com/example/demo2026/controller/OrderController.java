package com.example.demo2026.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo2026.entity.*;
import com.example.demo2026.repository.*;
import com.example.demo2026.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository dhRepo;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepo;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return (User) auth.getPrincipal();
    }

    @GetMapping
    public String viewOrders(Model model) {
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        if ("ROLE_ADMIN".equals(user.getRole())) {
            model.addAttribute("listOrders", dhRepo.findAll());
            return "orders"; // Admin view (tất cả đơn hàng)
        } else {
            model.addAttribute("listOrders", dhRepo.findByUser(user));
            return "shop/orders"; // Customer view (đơn hàng của tôi)
        }
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        try {
            String successMsg = orderService.checkout(cart, user);
            session.removeAttribute("cart");
            redirectAttributes.addFlashAttribute("successMessage", successMsg);
            return "redirect:/orders";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/orders";
    }
}