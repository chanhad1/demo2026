package com.example.demo2026.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo2026.entity.KhuyenMai;
import com.example.demo2026.entity.User;
import com.example.demo2026.repository.KhuyenMaiRepository;

@Controller
@RequestMapping("/promo")
public class PromoController {

    @Autowired
    private KhuyenMaiRepository kmRepo;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return (User) auth.getPrincipal();
    }

    @GetMapping
    public String promoPage(Model model) {
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("listPromo", kmRepo.findAll());

        if ("ROLE_ADMIN".equals(user.getRole())) {
            model.addAttribute("promo", new KhuyenMai());
            return "promo"; // Admin view (CRUD)
        } else {
            return "shop/promo"; // Customer view (View only)
        }
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@ModelAttribute KhuyenMai km) {
        kmRepo.save(km);
        return "redirect:/promo";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("promo", kmRepo.findById(id).orElse(new KhuyenMai()));
        model.addAttribute("listPromo", kmRepo.findAll());
        return "promo";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        kmRepo.deleteById(id);
        return "redirect:/promo";
    }
}