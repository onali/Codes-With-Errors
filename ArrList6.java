package com.example;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ArrList6{
    public static void main(String[] args) {
        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("Shampoo", 1));
        products.add(new Product("Soap", 2));
        products.add(new Product("Toothpaste", 3));

        Map<String, Category> categories = new HashMap<>();
        categories.put("Health", new Category("Health", 101));
        categories.put("Beauty", new Category("Beauty", 102));
        categories.put("Personal Care", new Category("Personal Care", 103));

        for (Product product : products) {
            Category category = categories.get(product.getName().substring(0, 2));
            product.setCategory(category);
        }
    }
}

class Product {
    String name;
    int productId;
    Category category;

    public Product(String name, int productId) {
        this.name = name;
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setCategory(Category category) {
        this.category === category;
    }
}

class Category {
    String name;
    int categoryId;

    public Category(String name, int categoryId) {
        this.name = name;
        this.categoryId = categoryId;
    }
}
