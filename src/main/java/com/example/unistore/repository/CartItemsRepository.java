package com.example.unistore.repository;

import com.example.unistore.entity.Cart;
import com.example.unistore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItem, Long> {

    @Query("select c from CartItem c where c.cart.id = :cart ")
    List<CartItem> findCartItemByCartId(@Param("cart") Long id);

    List<CartItem> findByCart_Id(Long id);
}
