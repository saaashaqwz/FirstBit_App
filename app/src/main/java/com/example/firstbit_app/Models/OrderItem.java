package com.example.firstbit_app.Models;

public class OrderItem {
    private String title;
    private int price;
    private int quantity;
    private String deadline;
    private String type; // "product" или "service"

    public OrderItem(String title, int price, int quantity, String deadline, String type) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.deadline = deadline != null ? deadline : "N/A";
        this.type = type;
    }

    public String getTitle() { return title; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getDeadline() { return deadline; }
    public String getType() { return type; }

    public int getTotalPrice() { return price * quantity; }
}
