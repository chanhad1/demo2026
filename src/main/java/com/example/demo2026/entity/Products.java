package com.example.demo2026.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private double price;
    
    @Column(name = "image_url")
    private String imageUrl;

    private String category;
    
    private int stock;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private double rating = 5.0;

    @ManyToOne
    @JoinColumn(name = "km_id")
    private KhuyenMai khuyenMai;

    public Products() {}

    public Products(Long id, String name, double price, String imageUrl, String category, int stock, String description, double rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stock = stock;
        this.description = description;
        this.rating = rating;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(KhuyenMai khuyenMai) { this.khuyenMai = khuyenMai; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    // Helper to get price after discount
    public double getDiscountedPrice() {
        if (khuyenMai == null) return price;
        return price - (price * khuyenMai.getDiscount() / 100.0);
    }
}