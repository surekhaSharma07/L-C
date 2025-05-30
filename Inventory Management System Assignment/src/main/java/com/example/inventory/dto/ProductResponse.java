package com.example.inventory.dto;

public class ProductResponse {
    private final int id;
    private final String name;
    private final int quantity;

    public ProductResponse(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "id=" + id +
                ", name=" + name  +
                ", quantity=" + quantity +
                '}';
    }
}
