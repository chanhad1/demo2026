package com.example.demo2026.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.demo2026.entity.Products;
import com.example.demo2026.entity.User;
import com.example.demo2026.repository.ProductsRepository;
import com.example.demo2026.repository.KhuyenMaiRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsRepository spRepo;

    @Autowired
    private KhuyenMaiRepository kmRepo;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return (User) auth.getPrincipal();
    }

    @GetMapping("/api/suggest")
    @ResponseBody
    public List<Map<String, Object>> suggestProducts(@RequestParam("q") String q) {
        if(q == null || q.trim().isEmpty()) return new ArrayList<>();
        List<Products> list = spRepo.findTop5ByNameContainingIgnoreCase(q.trim());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Products p : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("imageUrl", p.getImageUrl());
            map.put("price", p.getPrice());
            map.put("discountedPrice", p.getDiscountedPrice());
            result.add(map);
        }
        return result;
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "minPrice", required = false) Double minPrice,
                                 @RequestParam(value = "maxPrice", required = false) Double maxPrice,
                                 @RequestParam(value = "category", required = false) String category,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 Model model) {
        
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        int pageSize = 8;
        Pageable pageable = PageRequest.of(page, pageSize);
        
        if(keyword != null && keyword.trim().isEmpty()) keyword = null;
        if(category != null && category.trim().isEmpty()) category = null;

        Page<Products> productPage = spRepo.searchAdvanced(keyword, minPrice, maxPrice, category, pageable);
        
        model.addAttribute("productPage", productPage);
        model.addAttribute("listSanPham", productPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("category", category);
        
        List<String> categories = new ArrayList<>();
        for (Products p : spRepo.findAll()) {
            if (p.getCategory() != null && !p.getCategory().isEmpty() && !categories.contains(p.getCategory())) {
                categories.add(p.getCategory());
            }
        }
        model.addAttribute("categories", categories);

        return "shop/search";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        Products sp = spRepo.findById(id).orElse(null);
        if (sp == null) {
            return "redirect:/products";
        }

        model.addAttribute("product", sp);
        
        List<Products> related = new ArrayList<>();
        if (sp.getCategory() != null && !sp.getCategory().isEmpty()) {
            for (Products p : spRepo.findAll()) {
                if (sp.getCategory().equals(p.getCategory()) && !p.getId().equals(id)) {
                    related.add(p);
                }
            }
        } else {
            // Default: just get some products
             for (Products p : spRepo.findAll()) {
                if (!p.getId().equals(id)) {
                    related.add(p);
                }
            }
        }
        
        if (related.size() > 4) {
            related = related.subList(0, 4);
        }
        model.addAttribute("relatedProducts", related);

        return "shop/product-detail";
    }

    @GetMapping
    public String products(Model model) {
        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("listSanPham", spRepo.findAll());

        if ("ROLE_ADMIN".equals(user.getRole())) {
            model.addAttribute("sanpham", new Products());
            model.addAttribute("listPromo", kmRepo.findAll());
            return "products";
        } else {
            return "shop/products";
        }
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@ModelAttribute Products sp,
                       @RequestParam("imageFile") MultipartFile imageFile) {

        File uploadPath = new File(UPLOAD_DIR);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        if (!imageFile.isEmpty()) {
            try {
                String originalFileName = imageFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID().toString() + fileExtension;

                Path path = Paths.get(UPLOAD_DIR + newFileName);
                Files.write(path, imageFile.getBytes());
                sp.setImageUrl("/uploads/" + newFileName);
            } catch (IOException e) {
                System.err.println("Lỗi lưu file: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (sp.getId() != null) {
            spRepo.findById(sp.getId()).ifPresent(existing -> sp.setImageUrl(existing.getImageUrl()));
        }

        spRepo.save(sp);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        Products sp = spRepo.findById(id).orElse(new Products());
        model.addAttribute("sanpham", sp);
        model.addAttribute("listSanPham", spRepo.findAll());
        model.addAttribute("listPromo", kmRepo.findAll());
        return "products";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        spRepo.findById(id).ifPresent(sp -> {
            if (sp.getImageUrl() != null) {
                String fileName = sp.getImageUrl().replace("/uploads/", "");
                File imageFile = new File(UPLOAD_DIR + fileName);
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
            spRepo.deleteById(id);
        });
        return "redirect:/products";
    }
}