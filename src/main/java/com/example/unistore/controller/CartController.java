package com.example.unistore.controller;

import com.example.unistore.dto.ApiResponse;
import com.example.unistore.dto.cart.CartDetailResponse;
import com.example.unistore.entity.CartItem;
import com.example.unistore.repository.CartItemsRepository;
import com.example.unistore.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @PostMapping("/cartDetail/{cartId}")
    public ResponseEntity<?> getCartDetail(@PathVariable Long cartId) {

        List<CartItem> cartItemOptional = cartItemsRepository.findByCart_Id(cartId);
        List<CartDetailResponse> response = new ArrayList<>();

        if (cartItemOptional.isEmpty()){
            return ResponseEntity.ok(ApiResponse.error("There's no item", null));
        }

        for (CartItem c : cartItemOptional){
            CartDetailResponse dto = new CartDetailResponse(c.getId(), c.getProductId().getName(), c.getQuantity(),c.getProductId().getPrice(), c.getProductId().getImageUrl());
            response.add(dto);
        }


        return ResponseEntity.ok(ApiResponse.success("Get Cart successfully", response));



    }
}
