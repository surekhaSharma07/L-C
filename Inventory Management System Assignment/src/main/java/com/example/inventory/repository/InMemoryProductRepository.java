package com.example.inventory.repository;

import com.example.inventory.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final Map<Integer, Product> store = new ConcurrentHashMap<>();

    @Override
    public void save(Product product) {
        store.put(product.getId(), product);
    }

    @Override
    public Product findById(int id) {
        return store.get(id);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(int id) {
        store.remove(id);
    }

    @Override
    public List<Product> searchByName(String name) {
        return store.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findLowStock(int threshold) {
        return store.values().stream()
                .filter(p -> p.getQuantity() < threshold)
                .collect(Collectors.toList());
    }
}
