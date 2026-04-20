package com.example.demo2026.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "khuyen_mai")
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ten;     // tên chương trình
    private String code;    // mã code
    private double discount; // % giảm

    public KhuyenMai() {}

    public Long getId() { return id; }
    
    public void setId(Long id) { this.id = id; }

    public String getTen() { return ten; }

    public void setTen(String ten) { this.ten = ten; }
    
    public String getCode() { return code; }
    
    public void setCode(String code) { this.code = code; }

    public double getDiscount() { return discount; }

    public void setDiscount(double discount) { this.discount = discount; }
}