package com.app.cat201app;

import java.io.Serializable;

public class Book implements Serializable {
    private final String title;
    private final String author;
    private final String isbn;
    private boolean isAvailable;
    private String borrowerName;

    public Book(String title, String author, String isbn, String borrowerName) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true;
        this.borrowerName = "";
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isAvailable() { return isAvailable; }
    public String getBorrowerName() { return borrowerName; }

    public boolean borrow(String borrowerName) {
        if (isAvailable) {
            this.isAvailable = false;
            this.borrowerName = borrowerName;
            return true;
        }
        return false;
    }

    public boolean returnBook() {
        if (!isAvailable) {
            this.isAvailable = true;
            this.borrowerName = "";
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Title: %s, Author: %s, ISBN: %s, Available: %s, Borrower: %s",
                title, author, isbn, isAvailable ? "Yes" : "No", borrowerName);
    }
}
