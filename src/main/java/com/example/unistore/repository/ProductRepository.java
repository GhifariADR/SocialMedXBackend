package com.example.unistore.repository;

import com.example.unistore.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Products, Long> {

    @Query("SELECT p FROM Products p WHERE p.name LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    Page<Products> findProductsByCriteria(@Param("keyword") String keyword, Pageable pageable);
}
