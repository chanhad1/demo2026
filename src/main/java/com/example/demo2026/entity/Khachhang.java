package com.example.demo2026.entity;

import jakarta.persistence.*;

@Entity
@Table(name="khach_hang")
public class Khachhang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String ten;

    @Column(nullable=false)
    private String email;

    public Khachhang() {}
    public Khachhang(String ten, String email) { this.ten = ten; this.email = email; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}