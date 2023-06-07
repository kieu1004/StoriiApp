package com.example.storiiapp.data;

public class Comment {
    String id;
    String idUser;
    String username;
    String avt;
    String cmt;
    String like;
    String time;
    String idProduct;

    public String getId() {
        return id;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public String getAvt() {
        return avt;
    }

    public String getCmt() {
        return cmt;
    }

    public String getLike() {
        return like;
    }

    public String getTime() {
        return time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Comment() {
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public Comment(String id, String idUser, String username, String avt, String cmt, String like, String time, String idProduct) {
        this.id = id;
        this.idUser = idUser;
        this.username = username;
        this.avt = avt;
        this.cmt = cmt;
        this.like = like;
        this.time = time;
        this.idProduct = idProduct;
    }
}
