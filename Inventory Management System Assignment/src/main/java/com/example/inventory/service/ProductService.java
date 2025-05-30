package com.example.inventory.service;

import com.example.inventory.dto.ProductRequest;
import com.example.inventory.dto.ProductResponse;
import java.util.List;

public interface ProductService {
    void addProduct(ProductRequest product);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(int id);
    void updateProduct(int id, ProductRequest product);
    void updateStock(int id, int quantity);
    void deleteProduct(int id);
    boolean isAvailable(int id);
    List<ProductResponse> searchProducts(String name);
    List<ProductResponse> getLowStockProducts(int threshold);
    void bulkAddProducts(List<ProductRequest> products);
}
