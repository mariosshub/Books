package com.example.books;

import java.io.Serializable;


public class Book implements Serializable {
    private String id;
    private String name;
    private String author;
    private double price;
    private String imgUrl;
    private String description;
    private int copies;
    private int amount;

    public Book(){}
    public Book(String id, String name, String author, double price, String imgUrl, String description, int copies, int amount) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.imgUrl = imgUrl;
        this.description = description;
        this.copies = copies;
        this.amount = amount;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getCopies() { return copies; }

    public int getAmount() { return amount; }

    public void setAmount(int amount) { this.amount = amount; }
}
