package com.example.books;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String firstName;
    private String lastName;
    private boolean isAdmin;
    private ArrayList<Book> booksToCart;

    public User(){}
    public User(String firstName, String lastName, boolean isAdmin, ArrayList<Book> booksToCart) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
        this.booksToCart = booksToCart;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean isAdmin() { return isAdmin; }

    public void setAdmin(boolean admin) { isAdmin = admin; }

    public ArrayList<Book> getBooksToCart() { return booksToCart; }

    public void setBooksToCart(ArrayList<Book> booksToCart) { this.booksToCart = booksToCart; }
}
