package com.example.storiiapp.data;

public class Product {
    String id;
    String name;
    String description;
    String image;
    String price;
    String old_price;
    String star;
    String sold;
    String review;
    String id_category;


    public Product(String id, String name, String description, String image, String price, String old_price, String star, String review, String sold, String id_category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.old_price = old_price;
        this.review = review;
        this.star = star;
        this.sold = sold;
        this.id_category = id_category;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getOld_price() {
        return old_price;
    }

    public void setOld_price(String old_price) {
        this.old_price = old_price;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getId_category() {
        return id_category;
    }

    public void setId_category(String id_category) {
        this.id_category = id_category;
    }

    public int getNewPrice() {
        int price = Integer.parseInt(getPrice());
        int old_price = Integer.parseInt(getOld_price());
        int percent = Math.round(((old_price - price) / (float) old_price) * 100);

        return percent;
    }
}
