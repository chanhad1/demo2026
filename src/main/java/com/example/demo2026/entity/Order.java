package com.example.demo2026.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "don_hang")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private int quantity;
    private double price;
    
    private java.time.LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Order() {}

    public Long getId() { return id; }

    public String getProductName() { return productName; }

    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }
    
    public java.time.LocalDateTime getOrderDate() { return orderDate; }
    
    public void setOrderDate(java.time.LocalDateTime orderDate) { this.orderDate = orderDate; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }
}