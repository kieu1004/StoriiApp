package com.example.storiiapp.data;

public class ProductCart {
    String id;
    String name;
    String price;
    String quantity;
    String image;
    long expiryTimeMillis;
    boolean status;

    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public long getExpiryTimeMillis() {
        return expiryTimeMillis;
    }
    public void setExpiryTimeMillis(long expiryTimeMillis) {
        this.expiryTimeMillis = expiryTimeMillis;
    }

    public ProductCart() {}
    public ProductCart(String id, String name, String price, String quantity, String image, long expiryTimeMillis,boolean status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.expiryTimeMillis = expiryTimeMillis;
        this.status = status;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPrice() {
        return price;
    }
    public String getQuantity() {
        return quantity;
    }
    public String getImage() {
        return image;
    }
}

