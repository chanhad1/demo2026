package com.example.demo2026.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo2026.entity.Order;
import com.example.demo2026.service.OrderService;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class StatsController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/stats")
    public String stats(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/";
        }

        List<Order> allOrders = orderService.getAllOrders();

        double totalRevenue = 0;
        int totalOrders = allOrders.size();
        double[] monthlyRevenue = new double[12];
        int currentYear = LocalDateTime.now().getYear();

        for (Order order : allOrders) {
            double amount = order.getPrice() * order.getQuantity();
            totalRevenue += amount;

            java.time.LocalDateTime dateToUse = (order.getOrderDate() != null) ? order.getOrderDate() : java.time.LocalDateTime.now();

            if (dateToUse.getYear() == currentYear) {
                int monthIndex = dateToUse.getMonthValue() - 1;
                monthlyRevenue[monthIndex] += amount;
            }
        }

        double avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        int bestMonthIndex = -1;
        double maxRev = -1;
        String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                              "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};

        for (int i = 0; i < 12; i++) {
            if (monthlyRevenue[i] > maxRev) {
                maxRev = monthlyRevenue[i];
                bestMonthIndex = i;
            }
        }

        String bestMonth = (totalRevenue > 0 && bestMonthIndex != -1) ? monthNames[bestMonthIndex] : "N/A";

        model.addAttribute("revenue", totalRevenue);
        model.addAttribute("orders", totalOrders);
        model.addAttribute("avgOrderValue", avgOrderValue);
        model.addAttribute("bestMonth", bestMonth);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("hasData", totalOrders > 0);

        return "stats";
    }
}