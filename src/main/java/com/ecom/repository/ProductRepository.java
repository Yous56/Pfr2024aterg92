package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Product;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Integer>{

    List<Product> findByIsActiveTrue();
    List<Product> findByCategory(String category);

}
