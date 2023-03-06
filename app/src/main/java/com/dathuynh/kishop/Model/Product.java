package com.dathuynh.kishop.Model;

import java.io.Serializable;

public class Product implements Serializable {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product(String image, String price, String title, int id) {
        this.image = image;
        this.price = price;
        this.title = title;
        this.id = id;
    }


    public int id;
    public String image;
    public String price;
    public String title;
}



