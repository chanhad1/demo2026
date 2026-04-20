package com.example.demo2026.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo2026.entity.Products;
import com.example.demo2026.entity.CartItem;
import com.example.demo2026.repository.ProductsRepository;

import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductsRepository spRepo;

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
    }

    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        if (!isAuthenticated()) {
            return "redirect:/";
        }
        List<CartItem> cart = getCart(session);
        model.addAttribute("cart", cart);

        double total = cart.stream().mapToDouble(CartItem::getTotal).sum();
        model.addAttribute("total", total);

        return "cart";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id,
                           @RequestParam(defaultValue = "1") int qty,
                           HttpSession session) {
        if (!isAuthenticated()) {
            return "redirect:/";
        }

        List<CartItem> cart = getCart(session);
        Products sp = spRepo.findById(id).orElse(null);

        if (sp == null) return "redirect:/products";

        for (CartItem item : cart) {
            if (item.getId().equals(id)) {
                item.setQuantity(item.getQuantity() + qty);
                return "redirect:/cart";
            }
        }

        cart.add(new CartItem(sp.getId(), sp.getName(), sp.getPrice(), qty));
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated()) {
            return "redirect:/";
        }
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getId().equals(id));
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String update(@RequestParam Long id,
                         @RequestParam int qty,
                         HttpSession session) {
        if (!isAuthenticated()) {
            return "redirect:/";
        }
        List<CartItem> cart = getCart(session);

        for (CartItem item : cart) {
            if (item.getId().equals(id)) {
                item.setQuantity(qty);
                break;
            }
        }
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(HttpSession session) {
        if (!isAuthenticated()) {
            return "redirect:/";
        }
        session.removeAttribute("cart");
        return "redirect:/cart";
    }
}