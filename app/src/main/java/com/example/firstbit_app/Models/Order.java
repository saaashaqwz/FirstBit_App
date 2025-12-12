package com.example.firstbit_app.Models;

/**
 * класс, представляющий модель заказа
 */
public class Order {
    private int id;
    private int userId;
    private String status;
    private String deadline;
    private int total;

    public Order() {}

    public Order(int id, int userId, String status, String deadline, int total) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.deadline = deadline;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}