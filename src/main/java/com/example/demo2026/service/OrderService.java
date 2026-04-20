package com.example.demo2026.service;

import com.example.demo2026.entity.*;
import com.example.demo2026.repository.OrderRepository;
import com.example.demo2026.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductsRepository productsRepo;

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Double calculateTotalRevenue() {
        Double total = orderRepo.getTotalRevenue();
        return (total != null) ? total : 0.0;
    }

    @Transactional(rollbackFor = Exception.class)
    public String checkout(List<CartItem> cart, User user) {
        StringBuilder resultMsg = new StringBuilder("Đặt hàng thành công! Số lượng còn lại: ");
        for (CartItem item : cart) {
            Products product = productsRepo.findById(item.getId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + item.getName()));
            
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng. Kho chỉ còn: " + product.getStock());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productsRepo.save(product);

            Order order = new Order();
            order.setProductName(item.getName());
            order.setQuantity(item.getQuantity());
            order.setPrice(item.getPrice());
            order.setOrderDate(java.time.LocalDateTime.now());
            order.setUser(user);
            orderRepo.save(order);

            logger.info("Đã trừ {} sản phẩm: {}. Số lượng còn lại: {}", item.getQuantity(), product.getName(), product.getStock());
            resultMsg.append(product.getName()).append(" (").append(product.getStock()).append("), ");
        }
        
        String finalMsg = resultMsg.toString();
        if (finalMsg.endsWith(", ")) {
            finalMsg = finalMsg.substring(0, finalMsg.length() - 2);
        }
        return finalMsg;
    }
    
    public void deleteOrder(Long id) {
        orderRepo.deleteById(id);
    }
}