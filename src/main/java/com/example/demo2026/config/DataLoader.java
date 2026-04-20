package com.example.demo2026.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo2026.entity.Products;
import com.example.demo2026.entity.User;
import com.example.demo2026.repository.ProductsRepository;
import com.example.demo2026.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductsRepository productRepo;

    // Dữ liệu seed: { tên sản phẩm, giá, đường dẫn ảnh }
    private static final Object[][] PRODUCTS = {
        {"Coca Cola",       10000, "/images/anhcocacola.jfif"},
        {"Pepsi",           10000, "/images/anhpepsi.jfif"},
        {"Trà Xanh 0 Độ",  12000, "/images/traxanh0do.jfif"},
        {"Bánh Quy Oreo",  25000, "/images/anhBanhQuyOreo.jfif"},
        {"Mì Hảo Hảo",     5000, "/images/anhmihaohao.jfif"},
        {"Khoai Tây Lays", 15000, "/images/anhKhoaiTayLays.jfif"},
        {"Kem Đánh Răng PS", 20000, "/images/anhKemDanhRangPS.jfif"},
        {"Dầu Gội Clear",  45000, "/images/anhDauGoiClear.jfif"},
        {"Sữa Tươi TH",    12000, "/images/anhSuaTuoiTH.jfif"},
        {"Sting",           8000, "/images/anhSting.jfif"},
        {"Bánh Poca",       7000, "/images/anhBanhPoca.jfif"},
        {"Bánh Nabati",    18000, "/images/anhnabati.jfif"},
        {"7Up",            10000, "/images/anh7up.jfif"},
        {"Đậu Xanh",       10000, "/images/anhdauxanh.jfif"},
    };

    @Override
    public void run(String... args) throws Exception {
        // ======== SEED ADMIN ========
        User admin = userRepo.findByUsername("admin");
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setRole("ROLE_ADMIN");
            userRepo.save(admin);
            System.out.println(">>> SEEDED ADMIN USER: admin/admin");
        } else if (!"ROLE_ADMIN".equals(admin.getRole())) {
            admin.setRole("ROLE_ADMIN");
            userRepo.save(admin);
            System.out.println(">>> UPDATED ADMIN ROLE TO ROLE_ADMIN");
        }

        // ======== CLEANUP BAD PRODUCTS ========
        java.util.List<Products> allProducts = productRepo.findAll();
        java.util.Set<String> validNames = new java.util.HashSet<>();
        for (Object[] data : PRODUCTS) validNames.add((String) data[0]);

        for (Products p : allProducts) {
            if (!validNames.contains(p.getName())) {
                System.out.println(">>> DELETING INVALID/CORRUPTED PRODUCT: " + p.getName());
                productRepo.delete(p);
            }
        }

        // ======== SEED / UPDATE PRODUCTS ========
        int created = 0, updated = 0;
        for (Object[] data : PRODUCTS) {
            String name     = (String) data[0];
            double price    = ((Number) data[1]).doubleValue();
            String imageUrl = (String) data[2];

            Products existing = productRepo.findByName(name);
            if (existing == null) {
                Products p = new Products();
                p.setName(name);
                p.setPrice(price);
                p.setImageUrl(imageUrl);
                productRepo.save(p);
                created++;
                System.out.println(">>> SEEDED PRODUCT: " + name);
            } else {
                // Cập nhật ảnh và giá nếu chưa đúng
                boolean changed = false;
                if (!imageUrl.equals(existing.getImageUrl())) {
                    existing.setImageUrl(imageUrl);
                    changed = true;
                }
                if (existing.getPrice() != price) {
                    existing.setPrice(price);
                    changed = true;
                }
                if (changed) {
                    productRepo.save(existing);
                    updated++;
                    System.out.println(">>> UPDATED PRODUCT: " + name);
                }
            }
        }
        System.out.println(">>> Products seeded: " + created + " new, " + updated + " updated.");
    }
}
