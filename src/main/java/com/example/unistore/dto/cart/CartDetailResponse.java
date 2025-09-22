package com.example.unistore.dto.cart;

import java.math.BigDecimal;

public class CartDetailResponse {

    private Long id;

    private String name;

    private Integer quantity;

    private BigDecimal price;

    private String imageUrl;


    public CartDetailResponse(Long id, String name, Integer quantity, BigDecimal price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
