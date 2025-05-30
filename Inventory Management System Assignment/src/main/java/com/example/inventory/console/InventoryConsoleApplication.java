package com.example.inventory.console;

import com.example.inventory.dto.ProductRequest;
import com.example.inventory.dto.ProductResponse;
import com.example.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InventoryConsoleApplication {

    @Autowired
    private ProductService productService;

    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> addProduct();
                case 2 -> viewAllProducts();
                case 3 -> viewProductById();
                case 4 -> updateProduct();
                case 5 -> updateStock();
                case 6 -> deleteProduct();
                case 7 -> checkAvailability();
                case 8 -> searchProducts();
                case 9 -> viewLowStock();
                case 10 -> bulkAddProducts();
                case 0 -> exit = true;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\nInventory Management System");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. View Product by ID");
        System.out.println("4. Update Product Info");
        System.out.println("5. Update Product Stock");
        System.out.println("6. Delete Product");
        System.out.println("7. Check Availability");
        System.out.println("8. Search Products");
        System.out.println("9. View Low Stock Products");
        System.out.println("10. Bulk Add Products");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    private void addProduct() {
        ProductRequest product = new ProductRequest();
        System.out.print("Enter Product ID: ");
        product.setId(Integer.parseInt(scanner.nextLine()));
        System.out.print("Enter Product Name: ");
        product.setName(scanner.nextLine());
        System.out.print("Enter Quantity: ");
        product.setQuantity(Integer.parseInt(scanner.nextLine()));
        productService.addProduct(product);
        System.out.println("Product added successfully.");
    }

    private void viewAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        products.forEach(System.out::println);
    }

    private void viewProductById() {
        System.out.print("Enter Product ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println(productService.getProductById(id));
    }

    private void updateProduct() {
        ProductRequest product = new ProductRequest();
        System.out.print("Enter Product ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter New Name: ");
        product.setName(scanner.nextLine());
        System.out.print("Enter New Quantity: ");
        product.setQuantity(Integer.parseInt(scanner.nextLine()));
        product.setId(id);
        productService.updateProduct(id, product);
        System.out.println("Product updated.");
    }

    private void updateStock() {
        System.out.print("Enter Product ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter New Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());
        productService.updateStock(id, quantity);
        System.out.println("Stock updated.");
    }

    private void deleteProduct() {
        System.out.print("Enter Product ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());
        productService.deleteProduct(id);
        System.out.println("Product deleted.");
    }

    private void checkAvailability() {
        System.out.print("Enter Product ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        boolean available = productService.isAvailable(id);
        System.out.println("Available: " + available);
    }

    private void searchProducts() {
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine();
        List<ProductResponse> products = productService.searchProducts(name);
        products.forEach(System.out::println);
    }

    private void viewLowStock() {
        System.out.print("Enter stock threshold: ");
        int threshold = Integer.parseInt(scanner.nextLine());
        List<ProductResponse> products = productService.getLowStockProducts(threshold);
        products.forEach(System.out::println);
    }

    private void bulkAddProducts() {
        System.out.print("How many products do you want to add? ");
        int count = Integer.parseInt(scanner.nextLine());
        List<ProductRequest> productRequests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ProductRequest product = new ProductRequest();
            System.out.print("Enter ID: ");
            product.setId(Integer.parseInt(scanner.nextLine()));
            System.out.print("Enter Name: ");
            product.setName(scanner.nextLine());
            System.out.print("Enter Quantity: ");
            product.setQuantity(Integer.parseInt(scanner.nextLine()));
            productRequests.add(product);
        }
        productService.bulkAddProducts(productRequests);
        System.out.println("Bulk products added.");
    }
}
