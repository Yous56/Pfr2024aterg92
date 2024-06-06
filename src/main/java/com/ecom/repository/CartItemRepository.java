package com.ecom.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.CartItem;
import com.ecom.model.UserDtls;
import java.util.List;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer>{

    // public List<CartItem> findByUserDtls(UserDtls user);
    public List<CartItem> findByUser(UserDtls user);
    
    // public List<CartItem> getByUserDtls(UserDtls user);

}