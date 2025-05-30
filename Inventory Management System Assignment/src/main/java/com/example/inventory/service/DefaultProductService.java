package com.example.inventory.service;

import com.example.inventory.dto.ProductRequest;
import com.example.inventory.dto.ProductResponse;
import com.example.inventory.exception.ProductNotFoundException;
import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public DefaultProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public void addProduct(ProductRequest request) {
        if (request == null || request.getId() <= 0) {
            throw new IllegalArgumentException("Invalid product request");
        }

        Product product = new Product(request.getId(), request.getName(), request.getQuantity());
        productRepository.save(product);
    }


    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getQuantity()))
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(int id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        return new ProductResponse(product.getId(), product.getName(), product.getQuantity());
    }


    @Override
    public void updateProduct(int id, ProductRequest request) {
        Product existing = productRepository.findById(id);
        if (existing == null) {
            throw new ProductNotFoundException("Product not found for update.");
        }

        existing.setName(request.getName());
        existing.setQuantity(request.getQuantity());
        productRepository.save(existing);
    }

    @Override
    public void updateStock(int id, int quantity) {
        Product existing = productRepository.findById(id);
        if (existing == null) {
            throw new ProductNotFoundException("Product not found for stock update.");
        }

        existing.setQuantity(quantity);
        productRepository.save(existing);
    }


    @Override
    public void deleteProduct(int id) {
        productRepository.delete(id);
    }


    @Override
    public boolean isAvailable(int id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product not found.");
        }
        return product.getQuantity() > 0;
    }

    @Override
    public List<ProductResponse> searchProducts(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty.");
        }

        return productRepository.searchByName(name).stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getQuantity()))
                .collect(Collectors.toList());
    }

    // View products with low stock
    @Override
    public List<ProductResponse> getLowStockProducts(int threshold) {
        return productRepository.findLowStock(threshold).stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getQuantity()))
                .collect(Collectors.toList());
    }

    // Add multiple products at once
    @Override
    public void bulkAddProducts(List<ProductRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Product list cannot be empty.");
        }

        for (ProductRequest request : requests) {
            Product product = new Product(request.getId(), request.getName(), request.getQuantity());
            productRepository.save(product);
        }
    }
}
