package com.example.firstbit_app.Models;

/**
 * класс, представляющий модель корзины
 */
public class Cart {
    private int id;
    private int userId;
    private int productId;
    private int serviceId;
    private String title;
    private int price;
    private String image;
    private String deadline;
    private int quantity;
    private String addedDate;
    private String type; // "product" или "service"

    public Cart() { }

    public Cart(int id, int userId, int productId, int serviceId, String title, int price, int quantity, String addedDate, String type) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.serviceId = serviceId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.addedDate = addedDate;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getAddedDate() { return addedDate; }
    public void setAddedDate(String addedDate) { this.addedDate = addedDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getTotalPrice() { return price * quantity; }
}
