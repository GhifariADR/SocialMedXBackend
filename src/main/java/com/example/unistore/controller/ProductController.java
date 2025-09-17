package com.example.unistore.controller;

import com.example.unistore.dto.ApiResponse;
import com.example.unistore.dto.product.AddProductRequest;
import com.example.unistore.dto.product.ProductSearchRequest;
import com.example.unistore.entity.Products;
import com.example.unistore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody AddProductRequest request){

        if (request.getName() == null || request.getDescription() == null || request.getPrice() == null){
            return ResponseEntity.ok(ApiResponse.error("Request is not valid", null));
        }

        Products newProduct = new Products();

        newProduct.setName(request.getName());
        newProduct.setDescription(request.getDescription());

        if (request.getImageUrl() == null) {
            newProduct.setImageUrl("dummy");
        } else {
            newProduct.setImageUrl(request.getImageUrl());
        }

        newProduct.setPrice(new BigDecimal(String.valueOf(request.getPrice())));
        newProduct.setCreatedAt(new Date());
        newProduct.setStock(request.getStock());

        productRepository.save(newProduct);

        return ResponseEntity.ok(ApiResponse.success("Product successfully created", null));
    }

    @PostMapping("/searchProduct")
    public ResponseEntity<?> searchProduct(@RequestBody ProductSearchRequest request){
        Sort.Direction direction = request.getDirection().equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(direction, request.getSort()));

        Page<Products> products;

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()){
            products = productRepository.findProductsByCriteria(request.getKeyword(), pageable);
        } else {
            products = productRepository.findAll(pageable);
        }
        Map<String, Object> response = new HashMap<>();

        response.put("products", products.getContent());
        response.put("totalPages", products.getTotalPages());
        response.put("totalElements", products.getTotalElements());

        if (products.getContent().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("Product is empty", response));
        }

        return ResponseEntity.ok(ApiResponse.success("Product found", response));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody AddProductRequest request){

        Optional<Products> productsOptional = productRepository.findById(id);

        if (!productsOptional.isPresent()){
            return ResponseEntity.ok(ApiResponse.error("Product not found", null));
        }

        Products products = productsOptional.get();

        if (request.getName() != null){
            products.setName(request.getName());
        }

        if (request.getPrice() != null){
            products.setPrice(request.getPrice());
        }

        if (request.getStock() != null){
            products.setStock(request.getStock());
        }

        if (request.getDescription() != null){
            products.setDescription(request.getDescription());
        }

        if (request.getImageUrl() != null){
            products.setImageUrl(request.getImageUrl());
        }

        productRepository.save(products);

        return ResponseEntity.ok(ApiResponse.success("Product updated", null));


    }
}
