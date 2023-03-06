package com.dathuynh.kishop.Model;

import java.io.Serializable;

public class ProductDetail {
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCatogary() {
        return catogary;
    }

    public void setCatogary(String catogary) {
        this.catogary = catogary;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getInstock() {
        return instock;
    }

    public void setInstock(String instock) {
        this.instock = instock;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String image;

    public String getImage_adding() {
        return image_adding;
    }

    public void setImage_adding(String image_adding) {
        this.image_adding = image_adding;
    }

    public String image_adding;
    public String catogary;
    public double price;
    public long id;
    public String title;
    public String slug;
    public String instock;
}
