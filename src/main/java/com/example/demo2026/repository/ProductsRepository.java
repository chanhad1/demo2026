package com.example.demo2026.repository;

import com.example.demo2026.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Long> {
    Products findByName(String name);

    List<Products> findTop5ByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Products p WHERE " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:category IS NULL OR :category = '' OR p.category = :category)")
    Page<Products> searchAdvanced(@Param("keyword") String keyword, 
                                  @Param("minPrice") Double minPrice, 
                                  @Param("maxPrice") Double maxPrice, 
                                  @Param("category") String category, 
                                  Pageable pageable);
}
