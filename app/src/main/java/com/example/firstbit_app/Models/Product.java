package com.example.firstbit_app.Models;

/**
 * класс, представляющий модель товара
 */
public class Product {
    private int id;
    private String image;
    private Category category;
    private String title;
    private String description;
    private String license;
    private int price;

    public Product(int id, String image, Category category, String title, String description, String license, int price) {
        this.id = id;
        this.image = image;
        this.category = category;
        this.title = title;
        this.description = description;
        this.license = license;
        this.price = price;
    }

    /**
     * конструктор для создания из базы данных (без Context)
     */
    public Product(int id, String image, int categoryId, String categoryTitle, String title, String description, String license, int price) {
        this.id = id;
        this.image = image;
        this.category = new Category(categoryId, categoryTitle);
        this.title = title;
        this.description = description;
        this.license = license;
        this.price = price;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}