package com.example.demo2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.demo2026.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT SUM(o.price * o.quantity) FROM Order o")
    Double getTotalRevenue();
    
    java.util.List<Order> findByUser(com.example.demo2026.entity.User user);
}