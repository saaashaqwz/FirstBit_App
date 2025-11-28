package com.example.firstbit_app.Models;

/**
 * класс, представляющий модель услуги
 */
public class Service {
    private int id;
    private String image;
    private Category category;
    private String title;
    private String deadline;
    private int price;

    public Service(int id, int categoryId, String categoryTitle, String title, String deadline, int price) {
        this.id = id;
        this.category = new Category(categoryId, categoryTitle);
        this.title = title;
        this.deadline = deadline;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}