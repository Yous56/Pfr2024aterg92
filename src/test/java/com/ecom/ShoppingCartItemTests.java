package com.ecom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.ecom.model.CartItem;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartItemRepository;
import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShoppingCartItemTests {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void testAddOneCartItem(){
      Product product = testEntityManager.find(Product.class, 6);
      UserDtls user = testEntityManager.find(UserDtls.class, 2);


      CartItem newItem= new CartItem();
      newItem.setUser(user);
      newItem.setProduct(product);
      newItem.setQuantity(1);

      CartItem savedCartItem = cartItemRepository.save(newItem);
      assertTrue(savedCartItem.getId()>0);



    }

    @Test
    public void testGetCartItemsByUser(){

      UserDtls user = new UserDtls();
      user.setId(2);

      List<CartItem> cartItems = cartItemRepository.findByUser(user);
      assertEquals(5, cartItems.size());
    }




}


