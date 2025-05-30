package com.example.inventory.repository;

import com.example.inventory.model.Product;
import java.util.List;

public interface ProductRepository {
    void save(Product product);
    Product findById(int id);
    List<Product> findAll();
    void delete(int id);
    List<Product> searchByName(String name);
    List<Product> findLowStock(int threshold);
}
